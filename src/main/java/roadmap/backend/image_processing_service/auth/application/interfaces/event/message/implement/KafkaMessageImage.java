package roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement;

import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.DestinationEvent;

import java.util.Map;

public record KafkaMessageImage(
     DestinationEvent destinationEvent,
     Map<String, Object> args,
     KafkaEvent event,
     String UUID
) implements KafkaMessage {
    @Override
    public DestinationEvent destinationEvent() {
        return this.destinationEvent;
    }
}
