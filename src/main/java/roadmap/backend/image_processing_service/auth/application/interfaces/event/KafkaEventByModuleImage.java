package roadmap.backend.image_processing_service.auth.application.interfaces.event;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageImage;


@Service
public interface KafkaEventByModuleImage {
    KafkaMessageImage saveImage(KafkaMessageAuth request);
    KafkaMessageImage updateImage(KafkaMessageAuth request);
    KafkaMessageImage getImage(KafkaMessageAuth request);
    KafkaMessageImage getAllImages(KafkaMessageAuth request);
    KafkaMessageImage transformImage(KafkaMessageAuth request);
}