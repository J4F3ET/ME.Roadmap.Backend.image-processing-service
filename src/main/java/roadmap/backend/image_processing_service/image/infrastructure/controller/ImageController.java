package roadmap.backend.image_processing_service.image.infrastructure.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.TransformRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.AuthKafkaRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final Utils utils;
    private final ImageStorageTemporary imageStorageTemporary;
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    public ImageController(
            Utils utils, ImageStorageTemporary imageStorageTemporary,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage
    ) {
        this.utils = utils;
        this.imageStorageTemporary = imageStorageTemporary;
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
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
    public void uploadImage(@NonNull HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        final String token = utils.extractToken(request);
        String jsonMessage = utils.converterObjectToStringJson(
                new AuthKafkaRequest(new String[]{token}, KafkaEventModuleImage.SAVE_IMAGE)
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

