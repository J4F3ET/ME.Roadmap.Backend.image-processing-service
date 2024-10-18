package roadmap.backend.image_processing_service.image.infrastructure.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
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
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final Utils utils;
    private final ImageStorageTemporary storageTemporary;
    private final KafkaProducerByModuleAuthModuleImage eventProducerToAuth;
    private final ImageStorage imageStorage;

    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage,
            ImageStorage imageStorage
    ){
        this.utils = utils;
        this.storageTemporary = imageStorageTemporary;
        this.eventProducerToAuth = kafkaProducerByModuleAuthModuleImage;
        this.imageStorage = imageStorage;
    }

    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String,String>> getImage(
            @NonNull HttpServletRequest request,
            @PathVariable("id") Integer id
    ) {
        final String uuid = UUID.randomUUID().toString();
        CompletableFuture<RequestKafkaImage> responseKafka = eventProducerToAuth.send(
                utils.converterObjectToStringJson(
                        new ResponseKafkaByAuth(
                                ModuleDestionationEvent.IMAGE,
                                Map.of("token", utils.extractToken(request)),
                                KafkaEventModuleImage.GET_IMAGE,
                                uuid
                        )
                ),
                uuid
        );

        final int idUser;
        final Map<String,String> imageDetails;
        try {
            idUser = Integer.parseInt(responseKafka.thenApply(r->r.args().get("user_id").toString()).get());
            CompletableFuture<Map<String,String>> completableFuture = imageStorage.getImageDetails(id,idUser);
            imageDetails = completableFuture.get();
        }catch (Exception e) {
            System.out.println("Controller image get image: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        responseKafka.thenAccept(eventProducerToAuth::remove);

        if (imageDetails == null)  return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(imageDetails);

    }
    @GetMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<HashMap<Integer,String>> getImages(
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

        CompletableFuture<RequestKafkaImage> result = eventProducerToAuth.send(jsonMessage,uuid);

        try {
            Integer idUser = Integer.parseInt(result.thenApply(r->r.args().get("user_id").toString()).get());
            result.thenAccept(eventProducerToAuth::remove);
            return imageStorage.getAllImages(idUser, page, limit).thenApply(ResponseEntity::ok).get();
        } catch (Exception e) {
            System.out.println("Controller image get all images: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> uploadImage(@NonNull HttpServletRequest request, @RequestParam("file") MultipartFile file){

        if(file.getOriginalFilename() == null)
            return ResponseEntity.noContent().build();

        final String uuid = UUID.randomUUID().toString();
        final String jsonMessage = utils.converterObjectToStringJson(
                new ResponseKafkaByAuth(
                        ModuleDestionationEvent.IMAGE,
                        Map.of("token",  utils.extractToken(request)),
                        KafkaEventModuleImage.SAVE_IMAGE,
                        uuid
                )
        );

        CompletableFuture<RequestKafkaImage> responseKafka = eventProducerToAuth.send(jsonMessage,uuid);

        final String[] metadataImage = file.getOriginalFilename().split("\\.");
        try{
            ImageDTO imageDTO = new ImageDTO(metadataImage[0],metadataImage[1], file.getBytes());
            Integer idUser = Integer.parseInt(responseKafka.thenApply(requestKafkaImage -> requestKafkaImage.args().get("user_id").toString()).get());
            CompletableFuture<String> responseSaveImage = imageStorage.saveImage(idUser,imageDTO);

            if (responseSaveImage.get().contains("Error"))
                return ResponseEntity.badRequest().body("Image save failed");

            return ResponseEntity.ok("Image uploaded");
        } catch (Exception e) {
            System.err.println("Controller image save image: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Image save failed");
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

