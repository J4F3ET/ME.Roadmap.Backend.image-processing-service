package roadmap.backend.image_processing_service.auth.application.interfaces.kafka;

import org.springframework.stereotype.Service;

@Service
public interface KafkaServiceModuleAuth {
    String userIdUsernameToken(String token);
    String usernameToken(String token);
    String userIdToken(String token);
}
