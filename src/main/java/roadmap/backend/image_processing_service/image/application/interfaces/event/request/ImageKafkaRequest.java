package roadmap.backend.image_processing_service.image.application.interfaces.event.request;

import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaEventModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.ModuleDestionationEvent;

import java.util.Map;

public record ImageKafkaRequest(ModuleDestionationEvent destinationEvent, Map<String, Object> args,KafkaEventModuleImage event) {
}
