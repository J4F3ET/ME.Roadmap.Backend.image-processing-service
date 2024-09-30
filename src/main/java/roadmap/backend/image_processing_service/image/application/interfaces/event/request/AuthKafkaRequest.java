package roadmap.backend.image_processing_service.image.application.interfaces.event.request;

import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaEventModuleImage;

public record AuthKafkaRequest(String[] args, KafkaEventModuleImage methodType) {}
