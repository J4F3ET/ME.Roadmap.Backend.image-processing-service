package roadmap.backend.image_processing_service.image.application.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface Utils {
    String extractToken(HttpServletRequest request);
    <T>String converterObjectToStringJson(T object);
}
