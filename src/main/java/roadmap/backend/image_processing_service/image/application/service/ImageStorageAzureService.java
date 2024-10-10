package roadmap.backend.image_processing_service.image.application.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageRepository;
import roadmap.backend.image_processing_service.image.domain.entity.ImageEntity;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.Future;

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

    @Nullable
    private InputStream parseInputStream(MultipartFile file){
        try {
            return file.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
    @NonNull
    private ImageEntity parseImageEntity(MultipartFile file){
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageName(file.getOriginalFilename());
        imageEntity.setFormat(file.getContentType());
        imageEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return imageEntity;
    }
    private BlobClient getBlobClient(String path) {
        return containerClient.getBlobClient(path);
    }
    @Override
    public String saveImage(Integer userId, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BlobClient blobClient = getBlobClient(userId + separator + file.getOriginalFilename());
            blobClient.upload(inputStream, file.getSize());
            return blobClient.getBlobUrl();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
    @Nullable
    @Override
    public String getImageUrl(Integer id) {
        ImageEntity imageEntity = imageRepository.findById(id).orElse(null);
        if (imageEntity == null)
            return null;
        return imageEntity.getImagePath();
    }


    @Override
    public Future<File> getImageFile(Integer id) {
        ImageEntity imageEntity = imageRepository.findById(id).orElse(null);
        if (imageEntity == null)
            return null;
        BlobClient blobClient = containerClient.getBlobClient(imageEntity.getImagePath());
        BinaryData binaryData = blobClient.downloadContent();
        return null;
    }

    @Transactional
    @Nullable
    @Override
    public String updateImage(Integer userId, MultipartFile file) {
        ImageEntity imageEntity = imageRepository.findByUserIdAndImageName(userId, file.getOriginalFilename()).orElse(null);
        if (imageEntity == null)
            return null;
        ImageEntity newImageEntity = parseImageEntity(file);
        newImageEntity.setId(imageEntity.getId());
        imageRepository.save(newImageEntity);
        return newImageEntity.getImagePath();
    }

    @Transactional
    @Override
    public void deleteImage(Integer userId, String imageName) {
        imageRepository.findByUserIdAndImageName(userId, imageName).ifPresent(imageEntity -> {
            BlobClient blobClient = containerClient.getBlobClient(imageEntity.getImagePath());
            blobClient.delete();
            imageRepository.delete(imageEntity);
        });
    }

    @Override
    public boolean isImageExists(Integer Id, String imageName) {
        return imageRepository.findByUserIdAndImageName(Id, imageName).isPresent();
    }

    @Override
    public Future<HashMap<String, File>> getAllImages(Integer Id) {
        return null;
    }
}
