package in.mynk.erase_Bg.controller;

import in.mynk.erase_Bg.entity.User;
import in.mynk.erase_Bg.service.ImageService;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackground(
            @RequestParam("image") MultipartFile image,
            @RequestParam("clerkId") String clerkId) {
        
        User user = userService.findByClerkId(clerkId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        if (user.getCredits() <= 0) {
            return ResponseEntity.badRequest().body("Insufficient credits");
        }

        return imageService.removeBackground(image, clerkId);
    }

    @PostMapping("/bulk-remove-background")
    public ResponseEntity<?> bulkRemoveBackground(
            @RequestParam("images") MultipartFile[] images,
            @RequestParam("clerkId") String clerkId) {
        
        User user = userService.findByClerkId(clerkId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        if (user.getCredits() < images.length) {
            return ResponseEntity.badRequest().body("Insufficient credits");
        }

        return imageService.bulkRemoveBackground(images, clerkId);
    }
}
