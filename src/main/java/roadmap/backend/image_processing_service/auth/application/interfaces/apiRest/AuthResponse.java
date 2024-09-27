package roadmap.backend.image_processing_service.auth.application.interfaces.apiRest;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "token"})
public record AuthResponse(String username, String token) {
}
