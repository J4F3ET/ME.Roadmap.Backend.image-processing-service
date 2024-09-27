package roadmap.backend.image_processing_service.image.application.interfaces;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;

@Service
public interface ImageStorage {
    @Async
    String saveImage(String username, MultipartFile file);
    File getImage(String username, String imageName);
    void deleteImage(String username, String imageName);
    boolean isImageExists(String username, String imageName);
    HashMap<String, File> getAllImages(String username);
}
