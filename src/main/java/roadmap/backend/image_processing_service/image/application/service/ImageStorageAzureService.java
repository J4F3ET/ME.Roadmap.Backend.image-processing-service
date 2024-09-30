package roadmap.backend.image_processing_service.image.application.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@Primary
public class ImageStorageAzureService implements ImageStorage {

    @Value("${azure.storage.connection-string}")
    private final String connectionString;

    @Value("${azure.storage.container-name}")
    private final String containerName;

    private final BlobContainerClient containerClient;

    private final ImageRepository imageRepository;

    public ImageStorageAzureService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName,
            ImageRepository imageRepository
    ) {
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.imageRepository = imageRepository;
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
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
    @Transactional
    @Nullable
    @Override
    public String saveImage(Integer userId, MultipartFile file) {
        InputStream inputStream = parseInputStream(file);

        if (inputStream == null)
            return null;

        ImageEntity imageEntity = parseImageEntity(file);
        imageEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        BlobClient blobClient = containerClient.getBlobClient(imageEntity.getImagePath());
        blobClient.upload(inputStream, file.getSize());

        imageEntity.setImagePath(blobClient.getBlobUrl());
        imageRepository.save(imageEntity);

        return imageEntity.getImagePath();
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
    public File getImageFile(Integer id) {
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
    public HashMap<String, File> getAllImages(Integer Id) {
        return null;
    }
}
