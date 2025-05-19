package in.mynk.erase_Bg.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ResponseEntity<?> removeBackground(MultipartFile image, String clerkId);
    ResponseEntity<?> bulkRemoveBackground(MultipartFile[] images, String clerkId);
} 