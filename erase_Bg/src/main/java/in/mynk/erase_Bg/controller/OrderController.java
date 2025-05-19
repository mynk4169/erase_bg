package in.mynk.erase_Bg.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.mynk.erase_Bg.dto.RazorpayOrderDTO;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.OrderService;
import in.mynk.erase_Bg.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RazorpayService razorpayService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String planId, Authentication authentication) throws RazorpayException {

        Map<String, Object> responseMap = new HashMap<>();
        EraseBgResponse response = null;
        //validation

        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            response = EraseBgResponse.builder()
                    .statusCode(HttpStatus.FORBIDDEN)
                    .success(false)
                    .data("User do not have permission to access resource")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            Order order = orderService.createOrder(planId, authentication.getName());
            RazorpayOrderDTO responseDTO = convertToDTO(order);
            response = EraseBgResponse.builder()
                    .success(true)
                    .data(responseDTO)
                    .statusCode(HttpStatus.CREATED)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response = EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(e.getMessage())
                    .success(false)
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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOrder(@RequestBody Map<String, Object> request) throws RazorpayException {
        EraseBgResponse response;
        try {
            if (request == null || !request.containsKey("razorpay_order_id")) {
                response = EraseBgResponse.builder()
                    .success(false)
                    .data("Missing required parameters")
                    .statusCode(HttpStatus.BAD_REQUEST)
                    .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String razorypayOrderId = request.get("razorpay_order_id").toString();
            Map<String, Object> returnValue = razorpayService.verifyPayment(razorypayOrderId);
            
            response = EraseBgResponse.builder()
                .success(true)
                .data(returnValue)
                .statusCode(HttpStatus.OK)
                .build();
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            response = EraseBgResponse.builder()
                .success(false)
                .data(e.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response = EraseBgResponse.builder()
                .success(false)
                .data("An unexpected error occurred")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}


