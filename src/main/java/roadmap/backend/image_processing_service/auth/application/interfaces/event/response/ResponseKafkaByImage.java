package roadmap.backend.image_processing_service.auth.application.interfaces.event.response;

import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.KafkaEventModuleAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.ModuleDestionationEvent;

import java.util.Map;

public record ResponseKafkaByImage(ModuleDestionationEvent destinationEvent, Map<String, Object> args, KafkaEventModuleAuth event) { }
