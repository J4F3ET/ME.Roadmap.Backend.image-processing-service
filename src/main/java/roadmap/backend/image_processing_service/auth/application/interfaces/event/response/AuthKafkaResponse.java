package roadmap.backend.image_processing_service.auth.application.interfaces.event.response;

import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventModuleAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.ModuleDestionationEvent;

import java.util.Map;

public record AuthKafkaResponse(ModuleDestionationEvent destionationEvent, Map<String, Object> args, KafkaEventModuleAuth event) { }
