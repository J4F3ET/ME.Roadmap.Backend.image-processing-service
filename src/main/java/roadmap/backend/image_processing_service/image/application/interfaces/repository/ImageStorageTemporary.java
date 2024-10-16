package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;

import java.io.File;
import java.io.IOException;

@Service
public interface ImageStorageTemporary {
    void uploadImage(String token, MultipartFile file) throws IOException;
    void uploadImage(String token, ImageDTO imageDTO);
    MultipartFile downloadFileImage(String token);
    ImageDTO downloadImageDTO(String token);
}
