package roadmap.backend.image_processing_service.image.application.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.ImageGetAllResponse;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageRepository;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.domain.entity.ImageEntity;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class ImageStorageAzureService implements ImageStorage {

    private final BlobContainerClient containerClient;
    private final ImageRepository imageRepository;

    public ImageStorageAzureService(
            BlobContainerClient containerClient, ImageRepository imageRepository
    ) {
        this.containerClient = containerClient;
        this.imageRepository = imageRepository;
    }
    @NonNull
    private BlobClient getBlobClient(@NonNull String path) {
        return containerClient.getBlobClient(path);
    }

    private boolean saveImageInCloud(@NonNull BlobClient blobClient, byte @NonNull [] image) {
        try(InputStream inputStream = new ByteArrayInputStream(image)) {
            blobClient.upload(inputStream, inputStream.available());
            return true;
        }catch (Exception e) {
            System.out.println("Error in saveImageInCloud: " + e.getMessage());
            return false;
        }
    }

    private boolean saveImageInRepository(Integer userId, String name, String format, String path) {
        if(imageRepository.existsByImageName(name))
            return false;

        ImageEntity imageEntity = new ImageEntity(name, format, path, userId);
        imageEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            imageRepository.save(imageEntity);
            return true;
        } catch (Exception e) {
            System.out.println("Error in saveImageInRepository: " + e.getMessage());
            return false;
        }
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<String> saveImage(
            @NonNull Integer userId,
            @NonNull String nameImage,
            @NonNull String formatImage,
            byte @NonNull [] image
    ) {

        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        BlobClient blobClient = getBlobClient(userId.toString() + "/" + nameImage+"."+formatImage);
        String path = blobClient.getBlobUrl().replace("%2F", "/");

        if(!saveImageInRepository(userId, nameImage, formatImage, path)) {
            completableFuture.complete("Error in saveImage: Repository");
            return completableFuture;
        }

        if(! saveImageInCloud(blobClient, image)) {
            completableFuture.complete("Error in saveImage: Cloud");
            return completableFuture;
        }

        completableFuture.complete(path);
        return completableFuture;
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<String> saveImage(@NonNull Integer userId, @NonNull ImageDTO imageDTO) {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        BlobClient blobClient = getBlobClient(userId.toString() + '/' + imageDTO.name()+"."+imageDTO.format());
        String path = blobClient.getBlobUrl().replace("%2F", "/");

        if(!saveImageInRepository(userId, imageDTO.name(), imageDTO.format(), path)) {
            completableFuture.complete("Error in saveImage: Repository");
            return completableFuture;
        }

        if(! saveImageInCloud(blobClient, imageDTO.image())) {
            completableFuture.complete("Error in saveImage: Cloud");
            return completableFuture;
        }

        completableFuture.complete(blobClient.getBlobUrl());
        return completableFuture;
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<String> getImageUrl(Integer id) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ImageEntity imageEntity = imageRepository.findById(id).orElse(null);

        if (imageEntity == null) {
            completableFuture.complete(null);
            return completableFuture;
        }

        completableFuture.complete(imageEntity.getImagePath());
        return completableFuture;
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<ImageDTO> getImageFile(Integer id) {
        ImageEntity imageEntity = imageRepository.findById(id).orElse(null);
        CompletableFuture<ImageDTO> completableFuture = new CompletableFuture<>();

        if (imageEntity == null) {
            completableFuture.complete(null);
            return completableFuture;
        }

        BlobClient blobClient = containerClient.getBlobClient(imageEntity.getImagePath());
        BinaryData binaryData = blobClient.downloadContent();

        completableFuture.complete(new ImageDTO(
            imageEntity.getImageName(),
            imageEntity.getFormat(),
            binaryData.toBytes()
        ));
        return completableFuture;
    }
    @Nullable
    @Async
    @Transactional
    @Override
    public CompletableFuture<String> updateImage(Integer userId,String nameImage,String formatImage, byte[] image) {

        final boolean imageExists = imageRepository.existsByImageName(nameImage);
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();

        if(!imageExists) return null;
        try {

            final CompletableFuture<String> saveImageResult = saveImage(userId, nameImage, formatImage, image);
            if (saveImageResult.get().contains("Error"))
                throw new Exception("Error saving image");
            completableFuture.complete(saveImageResult.get());
        }catch (Exception e){
            completableFuture.complete("Error: " + e.getMessage());
            return completableFuture;
        }
        return completableFuture;
    }
    @Nullable
    @Async
    @Transactional
    public CompletableFuture<String> updateImage(Integer userId,@NonNull ImageDTO imageDTO) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> imageExists = isImageExists(userId, imageDTO.name());
        try {
            if (!imageExists.get())
                throw new Exception("Image not found");

            CompletableFuture<String> saveImageResult = saveImage(userId, imageDTO);
            if (saveImageResult.get().contains("Error"))
                throw new Exception("Error saving image");

            completableFuture.complete(saveImageResult.get());
        }catch (Exception e){
            completableFuture.complete("Error: " + e.getMessage());
        }
        return completableFuture;
    }
    @Async
    @Transactional
    @Override
    public void deleteImage(Integer idImage) {
        imageRepository.findById(idImage).ifPresent(imageEntity -> {
            BlobClient blobClient = containerClient.getBlobClient(imageEntity.getImagePath());
            blobClient.delete();
            imageRepository.delete(imageEntity);
        });
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<Boolean> isImageExists(Integer Id, String imageName) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        completableFuture.complete(imageRepository.findByUserIdAndImageName(Id, imageName).orElse(null) != null);
        return completableFuture;
    }
    @Async
    @Transactional
    @Override
    public CompletableFuture<HashMap<String, String>> getAllImages(Integer Id,@NonNull Integer page,@NonNull Integer limit) {
        CompletableFuture<HashMap<String, String>> completableFuture = new CompletableFuture<>();
        try{
            List<ImageGetAllResponse> imageEntities = imageRepository.findByUserId(Id,PageRequest.of(page,limit));
            completableFuture.complete(parseListToHashMap(imageEntities));
        }catch (Exception e){
            System.out.println("Error getAllImages: " + e.getMessage());
            completableFuture.complete(new HashMap<>());
        }
        return completableFuture;
    }
    @NonNull
    private HashMap<String, String> parseListToHashMap(@NonNull List<ImageGetAllResponse> list) {
        System.out.println(list.size());
        return (HashMap<String, String>)list.stream().collect(
            Collectors.toMap(
                ImageGetAllResponse::imageName,
                ImageGetAllResponse::imagePath,
                (existing, replacement) -> replacement
            ));
    }
}
