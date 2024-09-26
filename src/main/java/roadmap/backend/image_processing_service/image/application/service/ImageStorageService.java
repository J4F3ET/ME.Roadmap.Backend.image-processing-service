package roadmap.backend.image_processing_service.image.application.service;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.ImageStorage;
import roadmap.backend.image_processing_service.image.domain.entity.ImageDTO;

import java.util.HashMap;

@Service
public class ImageStorageService implements ImageStorage {
    @Override
    public ImageDTO saveImage(Integer userId, byte[] image) {
        return null;
    }

    @Override
    public ImageDTO getImage(Integer userId, Integer imageId) {
        return null;
    }

    @Override
    public void deleteImage(Integer userId, Integer imageId) {

    }

    @Override
    public boolean isImageExists(Integer userId, Integer imageId) {
        return false;
    }

    @Override
    public HashMap<Integer, ImageDTO> getAllImages(Integer userId) {
        return null;
    }
}
