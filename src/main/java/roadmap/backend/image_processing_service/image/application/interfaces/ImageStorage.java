package roadmap.backend.image_processing_service.image.application.interfaces;

import roadmap.backend.image_processing_service.image.domain.entity.ImageDTO;

import java.util.HashMap;

public interface ImageStorage {
    ImageDTO saveImage(Integer userId, byte[] image);
    ImageDTO getImage(Integer userId, Integer imageId);
    void deleteImage(Integer userId, Integer imageId);
    boolean isImageExists(Integer userId, Integer imageId);
    HashMap<Integer, ImageDTO> getAllImages(Integer userId);
}
