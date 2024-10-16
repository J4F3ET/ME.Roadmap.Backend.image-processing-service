package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public interface ImageStorage {
    @Async
    CompletableFuture<String> saveImage(Integer userId,String nameImage,String formatImage, byte[] image);
    @Async
    CompletableFuture<String> saveImage(Integer userId, @NonNull ImageDTO imageDTO);
    @Async
    CompletableFuture<String> getImageUrl(Integer imageId);
    @Async
    CompletableFuture<ImageDTO> getImageFile(Integer imageId);
    @Async
    CompletableFuture<String> updateImage(Integer imageId,String nameImage,String formatImage, byte[] image);
    @Async
    void deleteImage(Integer idImage);
    @Async
    CompletableFuture<Boolean> isImageExists(Integer userId, String imageName);
    @Async
    CompletableFuture<HashMap<String, String>> getAllImages(Integer userId,Integer page, Integer limit);
}
