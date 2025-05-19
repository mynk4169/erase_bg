package in.mynk.erase_Bg.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.mynk.erase_Bg.entity.OrderEntity;
import in.mynk.erase_Bg.repository.OrderRepository;
import in.mynk.erase_Bg.service.OrderService;
import in.mynk.erase_Bg.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final RazorpayService razorpayService;
    private final OrderRepository orderRepository;

    private static final Map<String, PlanDetails> PLAN_DETAILS = Map.of(
            "Basic", new PlanDetails("Basic", 100, 499.00),
            "Premium", new PlanDetails("Premium", 250, 899.00),
            "Ultimate", new PlanDetails("Ultimate", 1000, 1499.00)
    );

    private record PlanDetails(String name, int credits, double amount) {

    }


    @Override
    public Order createOrder(String planId, String clerkId) throws RazorpayException {
        log.info("Creating order for plan: {} by user: {}", planId, clerkId);
        
        PlanDetails details = PLAN_DETAILS.get(planId);
        if (details == null) {
            String errorMessage = String.format("Invalid planId: %s", planId);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            log.info("Creating Razorpay order for amount: {} INR", details.amount());
            Order razorpayOrder = razorpayService.createOrder(details.amount(), "INR");

            if (razorpayOrder == null || razorpayOrder.get("id") == null) {
                String errorMessage = "Received null or invalid order from Razorpay";
                log.error(errorMessage);
                throw new RazorpayException(errorMessage);
            }

            log.info("Saving order to database for orderId: {}", razorpayOrder.get("id"));
            OrderEntity newOrder = OrderEntity.builder()
                    .clerkId(clerkId)
                    .plan(details.name())
                    .credits(details.credits())
                    .amount(details.amount())
                    .orderId(razorpayOrder.get("id"))
                    .build();

            orderRepository.save(newOrder);
            log.info("Successfully created order: {} for user: {}", razorpayOrder.get("id"), clerkId);
            return razorpayOrder;
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
}
