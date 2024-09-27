package roadmap.backend.image_processing_service.image.application.interfaces.kafka;

public record AuthKafkaRequest(String[] args, KafkaMethodTypeAuth methodType) {}
