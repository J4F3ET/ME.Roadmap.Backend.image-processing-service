package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface ImageStorage {
    @Async
    CompletableFuture<String> saveImage(Integer userId,String nameImage,String formatImage, byte[] image);
    @Async
    CompletableFuture<String> saveImage(Integer userId, @NonNull ImageDTO imageDTO);
    @Async
    CompletableFuture<String> getImageUrl(Integer imageId,Integer userId);
    @Async
    CompletableFuture<ImageDTO> getImageFile(Integer imageId);
    @Async
    CompletableFuture<Map<String,String>> getImageDetails(Integer imageId, Integer userId);
    @Async
    CompletableFuture<String> updateImage(Integer imageId,String nameImage,String formatImage, byte[] image);
    @Async
    void deleteImage(Integer idImage);
    @Async
    CompletableFuture<Boolean> isImageExists(Integer userId, String imageName);
    @Async
    CompletableFuture<HashMap<Integer, String>> getAllImages(Integer userId,Integer page, Integer limit);
}
