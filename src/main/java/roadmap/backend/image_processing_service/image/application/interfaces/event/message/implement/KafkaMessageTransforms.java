package roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement;

import roadmap.backend.image_processing_service.image.application.interfaces.apiRest.transform.FormatImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.component.DestinationEvent;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;

import java.io.Serial;

public record KafkaMessageTransforms(
        String UUID,
        DestinationEvent destinationEvent,
        KafkaEvent event,
        String name,
        FormatImage format,
        byte[] image
)implements KafkaMessage {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    public DestinationEvent destinationEvent() {
        return this.destinationEvent;
    }
}
