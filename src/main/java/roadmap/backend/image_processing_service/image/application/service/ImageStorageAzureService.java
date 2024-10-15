package roadmap.backend.image_processing_service.image.application.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageRepository;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.domain.entity.ImageEntity;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.io.File.separator;

@Service
@Primary
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
    private ImageEntity parseImageEntity(Integer userId,String name,String format,String path){
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageName(name);
        imageEntity.setFormat(format);
        imageEntity.setUserId(userId);
        imageEntity.setImagePath(path);
        imageEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return imageEntity;
    }
    @NonNull
    private BlobClient getBlobClient(@NonNull String path) {
        return containerClient.getBlobClient(path);
    }

    private boolean saveImageInCloud(BlobClient blobClient,byte[] image) {
        try(InputStream inputStream = new ByteArrayInputStream(image)) {
            blobClient.upload(inputStream, inputStream.available());
            return true;
        }catch (Exception e) {
            System.out.println("Error in saveImageInCloud: " + e.getMessage());
            return false;
        }

    }

    private boolean saveImageInRepository(Integer userId, String name, String format, String path) {
        ImageEntity imageEntity = parseImageEntity(userId,name, format, path);
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
    public CompletableFuture<String> saveImage(Integer userId, String nameImage, String formatImage, byte[] image) {
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        BlobClient blobClient = getBlobClient(userId + separator + nameImage+"."+formatImage);
        if(!saveImageInRepository(userId, nameImage, formatImage, blobClient.getBlobUrl())) {
            completableFuture.complete("Error in saveImage: Repository");
            return completableFuture;
        }
        if(! saveImageInCloud(blobClient, image)) {
            completableFuture.complete("Error in saveImage: Cloud");
            return completableFuture;
        }
        return completableFuture;
    }
    @Async
    @Transactional
    protected CompletableFuture<String> saveImage(Integer userId, @NonNull ImageDTO imageDTO) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        BlobClient blobClient = getBlobClient(userId + separator + imageDTO.name()+"."+imageDTO.format());
        if(!saveImageInRepository(userId, imageDTO.name(), imageDTO.format(), blobClient.getBlobUrl())) {
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
        final CompletableFuture<Boolean> imageExists = isImageExists(userId, nameImage);
        final CompletableFuture<String> completableFuture = new CompletableFuture<>();

        try {
            if(!imageExists.get())
                throw new Exception("Image not found");
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
    public CompletableFuture<HashMap<String, String>> getAllImages(Integer Id, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        CompletableFuture<HashMap<String, String>> completableFuture = new CompletableFuture<>();
        System.out.println("GetAllImages");
        Iterable<ImageEntity> imageEntities = imageRepository.findByUserId(Id);
        completableFuture.complete(parseListToHashMap(imageEntities));
        return completableFuture;
    }
    @NonNull
    private HashMap<String, String> parseListToHashMap(@NonNull Iterable<ImageEntity> list) {
        HashMap<String, String> hashMap = new HashMap<>();
        for (ImageEntity imageEntity : list) {
            hashMap.put(imageEntity.getImageName(), imageEntity.getImagePath());
        }
        return hashMap;
    }
}
