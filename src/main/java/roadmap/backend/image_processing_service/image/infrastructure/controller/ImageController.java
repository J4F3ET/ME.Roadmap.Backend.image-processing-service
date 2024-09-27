package roadmap.backend.image_processing_service.image.infrastructure.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.TransformRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.kafka.AuthKafkaRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.kafka.KafkaMethodTypeAuth;

import javax.imageio.ImageIO;
import java.util.Arrays;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final FolderStorage folderStorage;
    private final ImageStorage imageStorage;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public ImageController(
            ImageStorage imageStorage,
            FolderStorage folderStorage,
    @Qualifier("kafkaTemplateModuleImage") KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.folderStorage = folderStorage;
        this.imageStorage = imageStorage;
        this.kafkaTemplate = kafkaTemplate;
    }
    @NonNull
    private String extractToken(@NonNull  HttpServletRequest request) throws IllegalArgumentException {
        final String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer "))
            return null;
        return authorization.substring(7);
    }
    @NonNull
    public <T> String jsonString(T object){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public @ResponseBody ResponseEntity<String> getImage(@NonNull HttpServletRequest request, @PathVariable("id") Integer id) {
        final String token = this.extractToken(request);
        AuthKafkaRequest requestAuth = new AuthKafkaRequest(new String[]{token}, KafkaMethodTypeAuth.USERID_USERNAME_TOKEN);
        String jsonMessage = jsonString(requestAuth);
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_ImageProcessingService, jsonMessage);
        return ResponseEntity.ok(jsonMessage);
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


