package in.mynk.erase_Bg.service;

import org.springframework.web.multipart.MultipartFile;

public interface EraseBackgroundService {
     byte[] removeBackground(MultipartFile file);
}
