package roadmap.backend.image_processing_service.auth.application.interfaces.event;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.AuthKafkaResponse;


@Service
public interface KafkaEventByModuleImage {
    AuthKafkaResponse saveImage(String token);
    AuthKafkaResponse updateImage(String token);
    AuthKafkaResponse getImage(String token);
    AuthKafkaResponse getAllImages(String token);
    AuthKafkaResponse transformImage(String token);
}