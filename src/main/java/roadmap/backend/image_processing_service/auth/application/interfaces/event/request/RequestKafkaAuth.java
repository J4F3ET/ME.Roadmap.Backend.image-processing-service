package roadmap.backend.image_processing_service.auth.application.interfaces.event.request;

import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.KafkaEventModuleAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.ModuleDestionationEvent;

import java.util.Map;

public record RequestKafkaAuth(
        ModuleDestionationEvent destinationEvent,
        KafkaEventModuleAuth event,
        Map<String, Object> args,
        String UUID
){ }
