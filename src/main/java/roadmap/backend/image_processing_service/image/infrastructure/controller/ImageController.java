package roadmap.backend.image_processing_service.image.infrastructure.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.TransformRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform.FormatImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.MessagePropertiesConstants;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.DestinationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerImage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final Utils utils;
    private final ImageStorageTemporary storageTemporary;
    private final KafkaProducerImage producer;
    private final ImageStorage imageStorage;

    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary,
            @Qualifier("kafkaProducerImage") KafkaProducerImage producer,
            ImageStorage imageStorage
    ){
        this.utils = utils;
        this.storageTemporary = imageStorageTemporary;
        this.producer = producer;

        this.imageStorage = imageStorage;
    }

    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String,String>> getImage(
            @NonNull HttpServletRequest request,
            @PathVariable("id") Integer id
    ) {

        final String uuid = UUID.randomUUID().toString();
        KafkaMessage message = new KafkaMessageAuth(
                DestinationEvent.IMAGE,
                Map.of("token", utils.extractToken(request)),
                KafkaEvent.GET_IMAGE,
                uuid
        );
        CompletableFuture<KafkaMessageImage> responseKafka = producer.send(TopicConfigProperties.TOPIC_NAME_Auth, message);

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

        responseKafka.thenAccept(producer::remove);

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
        KafkaMessage message = new KafkaMessageAuth(
                DestinationEvent.IMAGE,
                Map.of(MessagePropertiesConstants.TOKEN, utils.extractToken(request)),
                KafkaEvent.GET_ALL_IMAGES,
                uuid
        );
        CompletableFuture<KafkaMessageImage> result = producer.send(TopicConfigProperties.TOPIC_NAME_Auth,message);

        try {
            Integer idUser = Integer.parseInt(result.thenApply(r->r.args().get(MessagePropertiesConstants.USER_ID).toString()).get());
            result.thenAccept(producer::remove);
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
        KafkaMessage message = new KafkaMessageAuth(
                DestinationEvent.IMAGE,
                Map.of(MessagePropertiesConstants.TOKEN,  utils.extractToken(request)),
                KafkaEvent.SAVE_IMAGE,
                uuid
        );
        CompletableFuture<KafkaMessageImage> responseKafka = producer.send(TopicConfigProperties.TOPIC_NAME_Auth,message);

        final String[] metadataImage = file.getOriginalFilename().split("\\.");
        try{
            ImageDTO imageDTO = new ImageDTO(metadataImage[0],metadataImage[1], file.getBytes());
            Integer idUser = Integer.parseInt(responseKafka.thenApply(requestKafkaImage ->
                    requestKafkaImage
                            .args()
                            .get(MessagePropertiesConstants.USER_ID)
                            .toString()
            ).get());
            CompletableFuture<String> responseSaveImage = imageStorage.saveImage(idUser,imageDTO);
            responseKafka.thenAccept(producer::remove);
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
        String uuid = UUID.randomUUID().toString();
        CompletableFuture<ImageDTO> futureImage = imageStorage.getImageFile(id);
        ImageDTO imageDTO;
        try {
            imageDTO = futureImage.get(10L, TimeUnit.SECONDS);
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        KafkaMessageTransforms message = new KafkaMessageTransforms(
                uuid,
                DestinationEvent.IMAGE,
                KafkaEvent.TRANSFORM_IMAGE,
                imageDTO.name(),
                transformRequest.getTransformations(),
                FormatImage.valueOf(imageDTO.format()),
                imageDTO.image()
        );
        CompletableFuture<KafkaMessageImage> responseKafka = producer.send(TopicConfigProperties.TOPIC_NAME_Transform,message);
        String body;
        try {
            body = responseKafka.get(3, TimeUnit.SECONDS).convertToJson();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(body);
    }
}

