package roadmap.backend.image_processing_service.auth.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventModuleAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.ModuleDestionationEvent;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.AuthKafkaResponse;

import java.util.Map;

@Service
public class KafkaEventByModuleImageService implements KafkaEventByModuleImage {
    private final JwtUtils jwtUtils;

    public KafkaEventByModuleImageService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    public AuthKafkaResponse saveImage(String token) {
        System.out.println("Save image desde module auth");
        // Necesita el id del usuario
        Integer userId = jwtUtils.extractId(token);
        return new AuthKafkaResponse(
                ModuleDestionationEvent.IMAGE,
                Map.of("userId", userId),
                KafkaEventModuleAuth.SAVE_IMAGE
        );
    }

    @Override
    public AuthKafkaResponse updateImage(String token) {
        return null;
    }

    @Override
    public AuthKafkaResponse getImage(String token) {
        return null;
    }

    @Override
    public AuthKafkaResponse getAllImages(String token) {
        return null;
    }

    @Override
    public AuthKafkaResponse transformImage(String token) {
        return null;
    }
}
