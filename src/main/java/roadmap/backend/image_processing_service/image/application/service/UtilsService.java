package roadmap.backend.image_processing_service.image.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
@Service
public class UtilsService implements Utils {
    @Override
    public String extractToken(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer "))
            return null;
        return authorization.substring(7);
    }

    @Override
    public <T> String converterObjectToStringJson(T object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
