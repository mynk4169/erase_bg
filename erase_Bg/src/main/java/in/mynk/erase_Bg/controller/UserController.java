package in.mynk.erase_Bg.controller;

import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDto userDto, Authentication authentication) {
        log.info("Received request to create/update user for clerkId: {}", userDto.getClerkId());
        
        EraseBgResponse response;
        try {
            if (authentication == null) {
                log.warn("Authentication is null for user creation request");
                response = EraseBgResponse.builder()
                        .success(false)
                        .data("Authentication required")
                        .statusCode(HttpStatus.UNAUTHORIZED)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!authentication.getName().equals(userDto.getClerkId())) {
                log.warn("Authentication mismatch. Expected: {}, Got: {}", userDto.getClerkId(), authentication.getName());
                response = EraseBgResponse.builder()
                        .success(false)
                        .data("User does not have permission to access the resource")
                        .statusCode(HttpStatus.FORBIDDEN)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UserDto user = userService.saveUser(userDto);
            log.info("Successfully created/updated user: {}", user.getClerkId());
            
            response = EraseBgResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.OK)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating/updating user: {}", e.getMessage(), e);
            response = EraseBgResponse.builder()
                    .success(false)
                    .data("Failed to create/update user: " + e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{clerkId}")
    public ResponseEntity<?> deleteUser(@PathVariable String clerkId, Authentication authentication) {
        EraseBgResponse response;

        if (!authentication.getName().equals(clerkId)) {
            response = EraseBgResponse.builder()
                    .success(false)
                    .data("Unauthorized")
                    .statusCode(HttpStatus.FORBIDDEN)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        userService.deleteUserByClerkId(clerkId);
        response = EraseBgResponse.builder()
                .success(true)
                .data("User deleted successfully")
                .statusCode(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(Authentication authentication) {
        try {
            if (authentication == null) {
                log.warn("Unauthorized credits request - authentication is null");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .data("User does not have permission to access resource")
                        .success(false)
                        .build());
            }

            String clerkId = authentication.getName();
            if (clerkId == null || clerkId.isEmpty()) {
                log.warn("Unauthorized credits request - clerkId is null or empty");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .data("User does not have permission to access resource")
                        .success(false)
                        .build());
            }

            log.debug("Fetching credits for user: {}", clerkId);
            UserDto existingUser = userService.getUserByClerkId(clerkId);
            
            if (existingUser == null) {
                log.warn("User not found for clerkId: {}", clerkId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(EraseBgResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND)
                        .data("User not found")
                        .success(false)
                        .build());
            }

            Map<String, Integer> map = new HashMap<>();
            map.put("credits", existingUser.getCredits());
            
            log.debug("Successfully retrieved credits for user {}: {}", clerkId, existingUser.getCredits());
            return ResponseEntity.ok(EraseBgResponse.builder()
                .statusCode(HttpStatus.OK)
                .data(map)
                .success(true)
                .build());

        } catch (Exception e) {
            log.error("Error getting user credits: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong while fetching credits")
                    .success(false)
                    .build());
        }
    }

}
