package roadmap.backend.image_processing_service.image.infrastructure.controller;


import com.azure.storage.blob.BlobContainerClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.TransformRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.ModuleDestionationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.response.ResponseKafkaByAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.application.service.ImageStorageAzureService;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final Utils utils;
    private final ImageStorageTemporary imageStorageTemporary;
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    private final ImageStorage imageStorage;

    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage,
            ImageStorage imageStorage
    ){
        this.utils = utils;
        this.imageStorageTemporary = imageStorageTemporary;
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
        this.imageStorage = imageStorage;
    }

    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void getImage(
            @PathVariable("id") Integer id,
            @NonNull HttpServletResponse response
    ) {

    }
    @GetMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<HashMap<String,String>> getImages(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        final CompletableFuture<HashMap<String, String>> completableFuture = imageStorage.getAllImages(6, page, limit);
        try {
            HashMap<String, String> hashMap = completableFuture.get();
            for (String key : hashMap.keySet()) {
                System.out.println(key);
                System.out.println(hashMap.get(key));
            }
            return completableFuture.thenApply(ResponseEntity::ok).get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void uploadImage(@NonNull HttpServletRequest request, @RequestParam("file") MultipartFile file){
        System.out.println("Uploading image...");
        final String token = utils.extractToken(request);
        String jsonMessage = utils.converterObjectToStringJson(
                new ResponseKafkaByAuth(
                        ModuleDestionationEvent.IMAGE,
                        Map.of("token", token),
                        KafkaEventModuleImage.SAVE_IMAGE,
                        UUID.randomUUID().toString()
                )
        );
        kafkaProducerByModuleAuthModuleImage.send(jsonMessage);
        try{
            ImageDTO imageDTO = new ImageDTO(file.getOriginalFilename().split("\\.")[0],file.getOriginalFilename().split("\\.")[1], file.getBytes());
            imageStorageTemporary.uploadImage(token, imageDTO);
        } catch (Exception e) {
            ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/images/{id}/transform")
    @PreAuthorize("hasRole('ROLE_USER')")
    public @ResponseStatus ResponseEntity<String> transformImage(
            @NonNull HttpServletRequest request,
            @PathVariable("id") Integer id,
            @RequestBody @Validated TransformRequest transformRequest
    ){
        return ResponseEntity.ok("Transformacion realizada");
    }
}

