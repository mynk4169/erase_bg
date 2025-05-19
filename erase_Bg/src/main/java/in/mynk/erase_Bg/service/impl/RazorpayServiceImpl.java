package in.mynk.erase_Bg.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.entity.OrderEntity;
import in.mynk.erase_Bg.repository.OrderRepository;
import in.mynk.erase_Bg.service.RazorpayService;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayServiceImpl implements RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret}")
    private String razorypayKeySecret;

    private final OrderRepository orderRepository;
    private final UserService userService;

    @Override
    public Order createOrder(Double amount, String currency) throws RazorpayException {
        try {
            String logMessage = String.format("Creating Razorpay order for amount: %s %s", amount, currency);
            log.info(logMessage);
            
            if (amount == null || amount <= 0) {
                String errorMessage = "Invalid amount: " + amount;
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (currency == null || currency.trim().isEmpty()) {
                String errorMessage = "Invalid currency: " + currency;
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (razorpayKeyId == null || razorpayKeyId.trim().isEmpty() || 
                razorypayKeySecret == null || razorypayKeySecret.trim().isEmpty()) {
                String errorMessage = "Razorpay credentials not configured";
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorypayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcptid" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            log.debug("Sending request to Razorpay: {}", orderRequest.toString());
            Order order = razorpayClient.orders.create(orderRequest);
            
            if (order == null || order.get("id") == null) {
                String errorMessage = "Received null or invalid order from Razorpay";
                log.error(errorMessage);
                throw new RazorpayException(errorMessage);
            }

            logMessage = String.format("Successfully created Razorpay order: %s", order.get("id"));
            log.info(logMessage);
            return order;
        } catch (RazorpayException e) {
            String errorMessage = String.format("Razorpay error creating order: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new RazorpayException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error creating order: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new RazorpayException(errorMessage, e);
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String razorpayOrderId) throws RazorpayException {
        Map<String, Object> returnValue = new HashMap<>();
        try {
            String logMessage = String.format("Starting payment verification for order: %s", razorpayOrderId);
            log.info(logMessage);
            
            if (razorpayOrderId == null || razorpayOrderId.trim().isEmpty()) {
                log.error("Invalid order ID provided");
                throw new IllegalArgumentException("Order ID cannot be null or empty");
            }

            // First check if order exists in our database
            OrderEntity existingOrder = orderRepository.findByOrderId(razorpayOrderId)
                    .orElseThrow(() -> {
                        String errorMessage = String.format("Order not found in database: %s", razorpayOrderId);
                        log.error(errorMessage);
                        return new RuntimeException("Order not found: " + razorpayOrderId);
                    });

            logMessage = String.format("Found order in database for clerkId: %s", existingOrder.getClerkId());
            log.info(logMessage);

            if (existingOrder.getPayment()) {
                logMessage = String.format("Payment already processed for order: %s", razorpayOrderId);
                log.warn(logMessage);
                returnValue.put("success", false);
                returnValue.put("message", "Payment already processed");
                return returnValue;
            }

            // Verify with Razorpay
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorypayKeySecret);
            Order orderInfo = razorpayClient.orders.fetch(razorpayOrderId);
            
            String orderStatus = orderInfo.get("status").toString();
            logMessage = String.format("Razorpay order status: %s", orderStatus);
            log.info(logMessage);
            
            if ("paid".equalsIgnoreCase(orderStatus)) {
                // Get user and update credits
                UserDto userDto = userService.getUserByClerkId(existingOrder.getClerkId());
                int currentCredits = userDto.getCredits();
                int newCredits = currentCredits + existingOrder.getCredits();
                
                logMessage = String.format("Updating credits for user %s: %d + %d = %d", 
                    existingOrder.getClerkId(), currentCredits, existingOrder.getCredits(), newCredits);
                log.info(logMessage);
                
                userDto.setCredits(newCredits);
                userService.saveUser(userDto);
                
                // Mark order as paid
                existingOrder.setPayment(true);
                orderRepository.save(existingOrder);
                
                logMessage = String.format("Successfully processed payment and updated credits for order: %s", razorpayOrderId);
                log.info(logMessage);
                returnValue.put("success", true);
                returnValue.put("message", "Credits Added Successfully");
                return returnValue;
            } else {
                logMessage = String.format("Payment not completed for order: %s. Status: %s", razorpayOrderId, orderStatus);
                log.warn(logMessage);
                returnValue.put("success", false);
                returnValue.put("message", "Payment not completed. Status: " + orderStatus);
                return returnValue;
            }
        } catch (RazorpayException e) {
            String errorMessage = String.format("Razorpay error while verifying payment: %s", e.getMessage());
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error while verifying the payment: " + e.getMessage());
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error while verifying payment: %s", e.getMessage());
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Unexpected error while verifying payment: " + e.getMessage());
        }
    }
}
