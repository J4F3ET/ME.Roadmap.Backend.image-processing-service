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
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.ImageNameAndPath;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageRepository;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.domain.entity.ImageEntity;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public CompletableFuture<String> getImageUrl(Integer id,Integer userId) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ImageNameAndPath image;
        try {
            image = imageRepository.findByIdAndUserId(id,userId).orElseThrow();
        }catch (Exception e){
            System.err.println("Error in getImageUrl: " + e.getMessage());
            completableFuture.complete("Error in getImageUrl: " + e.getMessage());
            return completableFuture;
        }
        completableFuture.complete(image.imageName()+":"+image.imagePath());
        return completableFuture;
    }
    @Transactional
    @Override
    public CompletableFuture<ImageDTO> getImageFile(Integer id) {
        ImageEntity imageEntity;
        CompletableFuture<ImageDTO> completableFuture = new CompletableFuture<>();
        try {
            imageEntity = imageRepository.findById(id).orElseThrow();
        }catch (Exception e){
            System.err.println("Error in getImageFile: " + e.getMessage());
            completableFuture.complete(null);
            return completableFuture;
        }
        String url = imageEntity.getUserId().toString() + "/" + imageEntity.getImageName()+"."+imageEntity.getFormat();
        BlobClient blobClient = containerClient.getBlobClient(url);
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
    public CompletableFuture<Map<String,String>> getImageDetails(Integer imageId, Integer userId){
        CompletableFuture<Map<String,String>> completableFuture = new CompletableFuture<>();
        ImageEntity imageEntity;
        try {
            imageEntity = imageRepository.findById(imageId).orElseThrow();
        }catch (Exception e){
            System.err.println("Error in getImageDetails: " + e.getMessage());
            completableFuture.complete(null);
            return completableFuture;
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("ID", imageEntity.getId().toString());
        hashMap.put("Name",imageEntity.getImageName());
        hashMap.put("Format",imageEntity.getFormat());
        hashMap.put("Path",imageEntity.getImagePath());
        hashMap.put("Created",imageEntity.getCreatedAt().toString());
        hashMap.put("Updated",imageEntity.getUpdatedAt().toString());
        completableFuture.complete(hashMap);
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
    public CompletableFuture<HashMap<Integer, String>> getAllImages(Integer Id,@NonNull Integer page,@NonNull Integer limit) {
        CompletableFuture<HashMap<Integer, String>> completableFuture = new CompletableFuture<>();
        try{
            List<ImageNameAndPath> imageEntities = imageRepository.findByUserId(Id,PageRequest.of(page,limit));
            completableFuture.complete(parseListToHashMap(imageEntities));
        }catch (Exception e){
            System.out.println("Error getAllImages: " + e.getMessage());
            completableFuture.complete(new HashMap<>());
        }
        return completableFuture;
    }
    @NonNull
    private HashMap<Integer, String> parseListToHashMap(@NonNull List<ImageNameAndPath> list) {
        return (HashMap<Integer, String>)list.stream().collect(
            Collectors.toMap(ImageNameAndPath::id, ImageNameAndPath::imagePath, (existing, replacement) -> replacement));
    }
}
