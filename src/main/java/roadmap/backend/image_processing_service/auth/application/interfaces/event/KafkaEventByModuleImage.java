package roadmap.backend.image_processing_service.auth.application.interfaces.event;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.request.RequestKafkaAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.ResponseKafkaByImage;


@Service
public interface KafkaEventByModuleImage {
    ResponseKafkaByImage saveImage(RequestKafkaAuth request);
    ResponseKafkaByImage updateImage(RequestKafkaAuth request);
    ResponseKafkaByImage getImage(RequestKafkaAuth request);
    ResponseKafkaByImage getAllImages(RequestKafkaAuth request);
    ResponseKafkaByImage transformImage(RequestKafkaAuth request);
}