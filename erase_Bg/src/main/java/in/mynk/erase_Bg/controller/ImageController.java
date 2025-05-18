package in.mynk.erase_Bg.controller;

import in.mynk.erase_Bg.dto.UserDto;
import in.mynk.erase_Bg.response.EraseBgResponse;
import in.mynk.erase_Bg.service.EraseBackgroundService;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final EraseBackgroundService eraseBackgroundService;

    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackground(@RequestParam("file") MultipartFile file, Authentication authentication) {

        EraseBgResponse response = null;
        Map<String, Object> responseMap = new HashMap<>();

        try {
            //authentication
            if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
                response = EraseBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User do not have permission to access resource")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDto userDto = userService.getUserByClerkId(authentication.getName());

            //validation
            if (userDto.getCredits() == 0) {
                responseMap.put("message", "No credit balance");
                responseMap.put("creditBalance", userDto.getCredits());
                response = EraseBgResponse.builder()
                        .success(false)
                        .data(responseMap)
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .build();
                return ResponseEntity.ok(response);
            }
            byte[] imageBytes = eraseBackgroundService.removeBackground(file);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            userDto.setCredits(userDto.getCredits()-1);

            userService.saveUser(userDto);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(base64Image);

        } catch (Exception e) {
            e.printStackTrace(); // log to console
            response = EraseBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Something went wrong! ")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}
