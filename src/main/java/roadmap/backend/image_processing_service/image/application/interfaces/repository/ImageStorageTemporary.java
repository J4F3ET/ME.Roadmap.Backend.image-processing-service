package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
@Service
public interface ImageStorageTemporary {
    void uploadImage(String token, MultipartFile file);
    File downloadImage(String token);
}
