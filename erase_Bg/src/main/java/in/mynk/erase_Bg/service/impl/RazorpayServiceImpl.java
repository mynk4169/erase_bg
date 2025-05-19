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
            log.info("Creating Razorpay order for amount: {} {}", amount, currency);
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorypayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcptid" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);
            log.info("Successfully created Razorpay order: {}", order.get("id"));
            return order;
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: {}", e.getMessage());
            throw new RazorpayException("Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String razorpayOrderId) throws RazorpayException {
        Map<String, Object> returnValue = new HashMap<>();
        try {
            log.info("Starting payment verification for order: {}", razorpayOrderId);
            
            if (razorpayOrderId == null || razorpayOrderId.trim().isEmpty()) {
                log.error("Invalid order ID provided");
                throw new IllegalArgumentException("Order ID cannot be null or empty");
            }

            // First check if order exists in our database
            OrderEntity existingOrder = orderRepository.findByOrderId(razorpayOrderId)
                    .orElseThrow(() -> {
                        log.error("Order not found in database: {}", razorpayOrderId);
                        return new RuntimeException("Order not found: " + razorpayOrderId);
                    });

            log.info("Found order in database for clerkId: {}", existingOrder.getClerkId());

            if (existingOrder.getPayment()) {
                log.warn("Payment already processed for order: {}", razorpayOrderId);
                returnValue.put("success", false);
                returnValue.put("message", "Payment already processed");
                return returnValue;
            }

            // Verify with Razorpay
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorypayKeySecret);
            Order orderInfo = razorpayClient.orders.fetch(razorpayOrderId);
            
            String orderStatus = orderInfo.get("status").toString();
            log.info("Razorpay order status: {}", orderStatus);
            
            if ("paid".equalsIgnoreCase(orderStatus)) {
                // Get user and update credits
                UserDto userDto = userService.getUserByClerkId(existingOrder.getClerkId());
                int currentCredits = userDto.getCredits();
                int newCredits = currentCredits + existingOrder.getCredits();
                
                log.info("Updating credits for user {}: {} + {} = {}", 
                    existingOrder.getClerkId(), currentCredits, existingOrder.getCredits(), newCredits);
                
                userDto.setCredits(newCredits);
                userService.saveUser(userDto);
                
                // Mark order as paid
                existingOrder.setPayment(true);
                orderRepository.save(existingOrder);
                
                log.info("Successfully processed payment and updated credits for order: {}", razorpayOrderId);
                returnValue.put("success", true);
                returnValue.put("message", "Credits Added Successfully");
                return returnValue;
            } else {
                log.warn("Payment not completed for order: {}. Status: {}", razorpayOrderId, orderStatus);
                returnValue.put("success", false);
                returnValue.put("message", "Payment not completed. Status: " + orderStatus);
                return returnValue;
            }
        } catch (RazorpayException e) {
            log.error("Razorpay error while verifying payment: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error while verifying the payment: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while verifying payment: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Unexpected error while verifying payment: " + e.getMessage());
        }
    }
}
