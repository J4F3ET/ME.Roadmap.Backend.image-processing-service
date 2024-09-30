package roadmap.backend.image_processing_service.auth.application.interfaces.event.request;

import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventModuleAuth;

public record AuthKafkaRequest(String[] args, KafkaEventModuleAuth methodType) {}
