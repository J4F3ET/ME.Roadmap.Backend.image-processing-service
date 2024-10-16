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
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.RequestKafkaImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.response.ResponseKafkaByAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.application.service.ImageStorageAzureService;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.infrastructure.consumer.KafkaConsumerListenerModuleImage;
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
    private final KafkaConsumerListenerModuleImage kafkaConsumerListenerModuleImage;
    private final ImageStorage imageStorage;

    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage, KafkaConsumerListenerModuleImage kafkaConsumerListenerModuleImage,
            ImageStorage imageStorage
    ){
        this.utils = utils;
        this.imageStorageTemporary = imageStorageTemporary;
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
        this.kafkaConsumerListenerModuleImage = kafkaConsumerListenerModuleImage;
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
    public ResponseEntity<HashMap<String,String>> getImages(
            @NonNull HttpServletRequest request,
            @RequestParam(value = "page",defaultValue = "0") Integer page,
            @RequestParam(value ="limit",defaultValue = "10") Integer limit
    ) {
        String uuid = UUID.randomUUID().toString();
        String jsonMessage = utils.converterObjectToStringJson(
                new ResponseKafkaByAuth(
                        ModuleDestionationEvent.IMAGE,
                        Map.of("token", utils.extractToken(request)),
                        KafkaEventModuleImage.GET_ALL_IMAGES,
                        uuid
                )
        );
        CompletableFuture<RequestKafkaImage> result = kafkaProducerByModuleAuthModuleImage.sendWithUUID(jsonMessage,uuid);
        try {
            Integer idUser = Integer.parseInt(result.thenApply(r->r.args().get("user_id").toString()).get());
            result.thenAccept(kafkaProducerByModuleAuthModuleImage::remove);
            return imageStorage.getAllImages(idUser, page, limit).thenApply(ResponseEntity::ok).get();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> uploadImage(@NonNull HttpServletRequest request, @RequestParam("file") MultipartFile file){
        final String token = utils.extractToken(request);
        final String uuid = UUID.randomUUID().toString();
        String jsonMessage = utils.converterObjectToStringJson(
                new ResponseKafkaByAuth(
                        ModuleDestionationEvent.IMAGE,
                        Map.of("token", token),
                        KafkaEventModuleImage.SAVE_IMAGE,
                        uuid
                )
        );
        kafkaProducerByModuleAuthModuleImage.send(jsonMessage);
        try{
            ImageDTO imageDTO = new ImageDTO(
                    file.getOriginalFilename().split("\\.")[0],
                    file.getOriginalFilename().split("\\.")[1],
                    file.getBytes()
            );
            imageStorageTemporary.uploadImage(token, imageDTO);
            return ResponseEntity.ok("Image uploaded");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
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

