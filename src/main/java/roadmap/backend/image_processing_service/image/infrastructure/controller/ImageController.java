package roadmap.backend.image_processing_service.image.infrastructure.controller;


import com.azure.storage.blob.BlobContainerClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.auth.application.service.JwtUtils;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.TransformRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.ModuleDestionationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.response.ResponseKafkaByAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.application.service.ImageStorageAzureService;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final Utils utils;
    private final ImageStorageTemporary imageStorageTemporary;
    private final ImageStorageAzureService azureService;
    private final BlobContainerClient containerClient;
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    private final JwtUtils jwtUtils;

    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary, ImageStorageAzureService azureService, BlobContainerClient containerClient,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage,
            JwtUtils jwtUtils) {
        this.utils = utils;
        this.imageStorageTemporary = imageStorageTemporary;
        this.azureService = azureService;
        this.containerClient = containerClient;
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
        this.jwtUtils = jwtUtils;
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
    public void getImages(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        System.out.println("Hola");
        System.out.println(page);
        System.out.println(limit);
    }
    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void uploadImage(@NonNull HttpServletRequest request, @RequestParam("file") MultipartFile file){
        final String token = utils.extractToken(request);
        String jsonMessage = utils.converterObjectToStringJson(
                new ResponseKafkaByAuth(
                        ModuleDestionationEvent.IMAGE,
                        Map.of("token", token),
                        KafkaEventModuleImage.SAVE_IMAGE
                )
        );
        kafkaProducerByModuleAuthModuleImage.send(jsonMessage);
        imageStorageTemporary.uploadImage(token, file);
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

