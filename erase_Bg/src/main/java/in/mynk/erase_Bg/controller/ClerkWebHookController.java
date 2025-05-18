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
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class ClerkWebHookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final UserService userService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId, @RequestHeader("svix-timestamp") String svixTimestamp
            , @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {
        EraseBgResponse response = null;
        try {
            boolean isValid = verifyWebhookSignature(svixId, svixSignature, svixTimestamp);
            if (!isValid) {
                response = EraseBgResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED)
                        .data("Invalid webhook signature")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);

            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            response = EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong.")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }

    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId  = data.path("id").asText();
        userService.deleteUserByClerkId(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {

        String clerkId = data.path("id").asText();
        UserDto existingUser = userService.getUserByClerkId(clerkId);
        JsonNode emailNode = data.path("email_addresses");
        if (emailNode.isArray() && emailNode.size() > 0) {
            String email = emailNode.get(0).path("email_address").asText();
            existingUser.setEmail(email);
        }
        existingUser.setFirstName(data.path("first_name").asText());
        existingUser.setLastName(data.path("last_name").asText());
        existingUser.setPhotoUrl(data.path("image_url").asText());

        userService.saveUser(existingUser);


    }

    private void handleUserCreated(JsonNode data) {
        UserDto newUser = UserDto.builder()
                .clerkId(data.path("id").asText())
                .email(data.path("email_address").get(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .build();

        userService.saveUser(newUser);

    }
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        // Optionally log or process the webhook
        System.out.println("Received Clerk Webhook: " + payload);

        // Always return 200 to acknowledge receipt
        return ResponseEntity.ok("Webhook received");
    }

    private boolean verifyWebhookSignature(String svixId, String svixSignature, String svixTimestamp) {
        return true;
    }

}
