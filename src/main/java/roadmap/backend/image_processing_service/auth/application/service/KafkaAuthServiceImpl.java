package roadmap.backend.image_processing_service.auth.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.kafka.KafkaServiceModuleAuth;

import java.util.Map;

@Service
@Primary
public class KafkaAuthServiceImpl implements KafkaServiceModuleAuth {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    public KafkaAuthServiceImpl(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }
    @Nullable
    private String jsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public String userIdUsernameToken(String token) {
        String username = jwtUtils.extractUsername(token);
        Integer id = jwtUtils.extractId(token);
        return jsonString(Map.of("username", username, "id", id));
    }

    @Override
    public String usernameToken(String token) {
        return jsonString(Map.of("username", jwtUtils.extractUsername(token)));
    }

    @Override
    public String userIdToken(String token) {
        return jsonString(Map.of("id", jwtUtils.extractId(token)));
    }
}
