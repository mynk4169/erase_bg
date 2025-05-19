package in.mynk.erase_Bg.service.impl;

import in.mynk.erase_Bg.service.ImageService;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Value("${clipboard.api.key}")
    private String clipDropApiKey;

    private final UserService userService;
    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<?> removeBackground(MultipartFile image, String clerkId) {
        try {
            // Validate image
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest().body("No image provided");
            }

            // Process image and remove background
            byte[] processedImage = processImage(image.getBytes());
            
            // Deduct one credit
            userService.findByClerkId(clerkId).ifPresent(user -> {
                userService.updateCredits(clerkId, user.getCredits() - 1);
            });

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(processedImage);
        } catch (IOException e) {
            log.error("Error processing image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing image: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> bulkRemoveBackground(MultipartFile[] images, String clerkId) {
        try {
            // Validate images
            if (images == null || images.length == 0) {
                return ResponseEntity.badRequest().body("No images provided");
            }

            List<byte[]> processedImages = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    processedImages.add(processImage(image.getBytes()));
                }
            }

            // Deduct credits
            userService.findByClerkId(clerkId).ifPresent(user -> {
                userService.updateCredits(clerkId, user.getCredits() - images.length);
            });

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(processedImages);
        } catch (IOException e) {
            log.error("Error processing bulk images: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing images: " + e.getMessage());
        }
    }

    private byte[] processImage(byte[] imageBytes) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", clipDropApiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image_file", new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "image.png";
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://clipdrop-api.co/remove-background/v1",
                HttpMethod.POST,
                requestEntity,
                byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new IOException("Failed to process image: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error calling ClipDrop API: {}", e.getMessage());
            throw new IOException("Failed to process image: " + e.getMessage());
        }
    }
} 