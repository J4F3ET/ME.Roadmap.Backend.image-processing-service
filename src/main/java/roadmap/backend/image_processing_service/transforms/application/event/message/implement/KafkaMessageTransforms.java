package roadmap.backend.image_processing_service.transforms.application.event.message.implement;

import roadmap.backend.image_processing_service.transforms.application.event.component.DestinationEvent;
import roadmap.backend.image_processing_service.transforms.application.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.Transformation;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;

import java.io.Serial;

public record KafkaMessageTransforms(
        String UUID,
        DestinationEvent destinationEvent,
        KafkaEvent event,
        Transformation transformation,
        byte[] image,
        String name,
        FormatImage format
)implements KafkaMessage {
    @Serial
    private static final long serialVersionUID = 1L;
}
