package in.mynk.erase_Bg.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.mynk.erase_Bg.dto.RazorpayOrderDTO;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.OrderService;
import in.mynk.erase_Bg.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final RazorpayService razorpayService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String planId, Authentication authentication) {
        log.info("Creating order for plan: {} by user: {}", planId, authentication != null ? authentication.getName() : "unknown");
        
        try {
            if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
                log.warn("Unauthorized order creation attempt");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User does not have permission to access resource")
                        .build());
            }

            if (planId == null || planId.trim().isEmpty()) {
                log.warn("Invalid planId provided: {}", planId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .success(false)
                        .data("Invalid plan ID")
                        .build());
            }

            Order order = orderService.createOrder(planId, authentication.getName());
            if (order == null) {
                log.error("Order creation returned null for plan: {} and user: {}", planId, authentication.getName());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                        .success(false)
                        .data("Failed to create order")
                        .build());
            }

            RazorpayOrderDTO responseDTO = convertToDTO(order);
            log.info("Successfully created order: {} for user: {}", order.get("id"), authentication.getName());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(EraseBgResponse.builder()
                    .success(true)
                    .data(responseDTO)
                    .statusCode(HttpStatus.CREATED)
                    .build());

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(EraseBgResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .success(false)
                    .data(e.getMessage())
                    .build());
        } catch (RazorpayException e) {
            log.error("Razorpay error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Payment service error: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("An unexpected error occurred")
                    .build());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOrder(@RequestBody Map<String, Object> request) {
        log.info("Received payment verification request: {}", request);
        EraseBgResponse response;
        
        try {
            if (request == null || !request.containsKey("razorpay_order_id")) {
                log.warn("Invalid verification request: missing razorpay_order_id");
                response = EraseBgResponse.builder()
                    .success(false)
                    .data("Missing required parameters")
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String razorpayOrderId = request.get("razorpay_order_id").toString();
            Map<String, Object> verificationResult = razorpayService.verifyPayment(razorpayOrderId);
            
            log.info("Payment verification result for order {}: {}", razorpayOrderId, verificationResult);
            
            response = EraseBgResponse.builder()
                .success(true)
                .data(verificationResult)
                .statusCode(HttpStatus.OK)
                .build();
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            log.error("Razorpay error during verification: {}", e.getMessage(), e);
            response = EraseBgResponse.builder()
                .success(false)
                .data("Payment verification failed: " + e.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Unexpected error during verification: {}", e.getMessage(), e);
            response = EraseBgResponse.builder()
                .success(false)
                .data("An unexpected error occurred during verification")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private RazorpayOrderDTO convertToDTO(Order order) {
        return RazorpayOrderDTO.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_at(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();
    }
}


