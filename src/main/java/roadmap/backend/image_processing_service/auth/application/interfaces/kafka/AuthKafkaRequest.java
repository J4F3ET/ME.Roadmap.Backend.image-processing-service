package roadmap.backend.image_processing_service.auth.application.interfaces.kafka;

public record AuthKafkaRequest(String[] args, KafkaMethodType methodType) {}
