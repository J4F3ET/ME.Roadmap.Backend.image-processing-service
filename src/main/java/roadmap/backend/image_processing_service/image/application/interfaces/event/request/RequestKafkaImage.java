package roadmap.backend.image_processing_service.image.application.interfaces.event.request;

import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.ModuleDestionationEvent;

import java.util.Map;

public record RequestKafkaImage(ModuleDestionationEvent destinationEvent, Map<String, Object> args, KafkaEventModuleImage event) {
}
