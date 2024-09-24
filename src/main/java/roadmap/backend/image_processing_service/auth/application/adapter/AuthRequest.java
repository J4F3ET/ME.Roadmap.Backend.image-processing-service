package roadmap.backend.image_processing_service.auth.application.adapter;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public record AuthRequest(String username, String password) {}
