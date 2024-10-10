package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Future;

@Service
public interface ImageStorage {
    String saveImage(Integer id, MultipartFile file);
    String getImageUrl(Integer Id);
    Future<File> getImageFile(Integer Id);
    String updateImage(Integer id, MultipartFile file);
    void deleteImage(Integer Id, String imageName);
    boolean isImageExists(Integer id, String imageName);
    Future<HashMap<String, File>> getAllImages(Integer id);
}
