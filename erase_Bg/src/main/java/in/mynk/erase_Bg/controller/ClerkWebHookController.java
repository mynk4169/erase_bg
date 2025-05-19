package in.mynk.erase_Bg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class ClerkWebHookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestBody String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();
            JsonNode data = rootNode.path("data");

            switch (eventType) {
                case "user.created":
                    String clerkId = data.path("id").asText();
                    String email = data.path("email_addresses").get(0).path("email_address").asText();
                    String firstName = data.path("first_name").asText();
                    String lastName = data.path("last_name").asText();

                    userService.createUser(clerkId, email, firstName, lastName);
                    return ResponseEntity.ok().build();

                case "user.deleted":
                    clerkId = data.path("id").asText();
                    userService.deactivateUser(clerkId);
                    return ResponseEntity.ok().build();

                default:
                    return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid webhook payload: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        // Optionally log or process the webhook
        System.out.println("Received Clerk Webhook: " + payload);
        return ResponseEntity.ok("Webhook received");
    }

    private boolean verifyWebhookSignature(String svixId, String svixSignature, String svixTimestamp) {
        // Implement webhook signature verification
        return true;
    }
}
