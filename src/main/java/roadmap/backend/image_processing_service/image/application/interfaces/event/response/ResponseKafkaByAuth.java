package roadmap.backend.image_processing_service.image.application.interfaces.event.response;

import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.ModuleDestionationEvent;

import java.util.Map;

public record ResponseKafkaByAuth(
        ModuleDestionationEvent destinationEvent,
        Map<String, Object> args,
        KafkaEventModuleImage event,
        String UUID
) { }
