package roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement;

import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.DestinationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;

import java.io.Serial;
import java.util.Map;

public record KafkaMessageAuth(
        DestinationEvent destinationEvent,
        Map<String, Object> args,
        KafkaEvent event,
        String UUID
)implements KafkaMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public DestinationEvent destinationEvent() {
        return this.destinationEvent;
    }
}