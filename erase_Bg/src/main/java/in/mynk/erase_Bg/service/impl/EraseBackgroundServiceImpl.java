package in.mynk.erase_Bg.service.impl;

import in.mynk.erase_Bg.client.ClipdropClient;
import in.mynk.erase_Bg.service.EraseBackgroundService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EraseBackgroundServiceImpl implements EraseBackgroundService {


    @Value("${clipboard.api.key}")
    private String apiKey;

    private final ClipdropClient clipdropClient;

    @Override
    public byte[] removeBackground(MultipartFile file) {
        return clipdropClient.eraseBackground(file,apiKey);
    }
}
