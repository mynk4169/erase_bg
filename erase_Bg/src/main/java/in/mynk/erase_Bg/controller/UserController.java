package in.mynk.erase_Bg.controller;

import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDto userDto, Authentication authentication) {
        EraseBgResponse response;

        if (authentication == null) {
            response = EraseBgResponse.builder()
                    .success(false)
                    .data("Authentication required")
                    .statusCode(HttpStatus.UNAUTHORIZED)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!authentication.getName().equals(userDto.getClerkId())) {
            response = EraseBgResponse.builder()
                    .success(false)
                    .data("User does not have permission to access the resource")
                    .statusCode(HttpStatus.FORBIDDEN)
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            UserDto user = userService.saveUser(userDto);
            response = EraseBgResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.OK)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response = EraseBgResponse.builder()
                    .success(false)
                    .data(e.getMessage())
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
        EraseBgResponse bgResponse = null;
        try {
            if (authentication.getName().isEmpty() || authentication.getName() == null) {
                EraseBgResponse.builder().statusCode(HttpStatus.FORBIDDEN)
                        .data("user doesnot have permission to access resource")
                        .success(false);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(bgResponse);
            }

            String clerkId = authentication.getName();
            UserDto existingUser = userService.getUserByClerkId(clerkId);
            Map<String, Integer> map = new HashMap<>();
            map.put("credits", existingUser.getCredits());
            bgResponse = EraseBgResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .data(map)
                    .success(true)
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bgResponse);

        }catch (Exception e){
            bgResponse = EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong!")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(bgResponse);

        }
    }

}
