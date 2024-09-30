package roadmap.backend.image_processing_service.image.application.interfaces.event.response;

import roadmap.backend.image_processing_service.image.application.interfaces.event.ModuleDestionationEvent;

public record ImageKafkaResponse(ModuleDestionationEvent destinationEvent, String methodType, String[] args) {
}
