package roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement;

import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.DestinationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;

import java.io.Serial;
import java.util.Map;

public record KafkaMessageImage (
        DestinationEvent destinationEvent,
        KafkaEvent event,
        Map<String, Object> args,
        String UUID
)implements KafkaMessage {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    public DestinationEvent destinationEvent() {
        return this.destinationEvent;
    }
}