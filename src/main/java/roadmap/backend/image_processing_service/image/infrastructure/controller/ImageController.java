package roadmap.backend.image_processing_service.image.infrastructure.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.auth.application.service.JwtUtils;
import roadmap.backend.image_processing_service.image.application.config.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.ImageRepository;

@Controller
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class ImageController {
    private final JwtUtils jwtUtils;
    private final ImageRepository imageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public ImageController(JwtUtils jwtUtils, ImageRepository imageRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.jwtUtils = jwtUtils;
        this.imageRepository = imageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    @GetMapping("/images/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public @ResponseBody void getImage(@NonNull HttpServletRequest request, @PathVariable("id") Integer id) {
        final Integer userId = jwtUtils.extractUserIdByRequest(request);

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
    public void uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME, file.getOriginalFilename());
    }
}
