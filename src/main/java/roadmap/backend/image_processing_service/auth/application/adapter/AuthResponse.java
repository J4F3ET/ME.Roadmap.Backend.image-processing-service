package roadmap.backend.image_processing_service.auth.application.adapter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "token"})
public record AuthResponse(String username, String token) {
}
