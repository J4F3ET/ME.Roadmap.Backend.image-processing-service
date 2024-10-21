package roadmap.backend.image_processing_service.auth.application.service;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.MessagePropertiesConstants;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.message.implement.KafkaMessageImage;

import java.util.Map;

@Service
public class KafkaEventByModuleImageService implements KafkaEventByModuleImage {

    private final JwtUtils jwtUtils;

    public KafkaEventByModuleImageService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    public KafkaMessageImage saveImage(KafkaMessageAuth request) {
        String token = request.args().get(MessagePropertiesConstants.TOKEN).toString();

        if (token == null) return null;

        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of(MessagePropertiesConstants.USER_ID, userId, MessagePropertiesConstants.TOKEN, token));
    }

    @Override
    public KafkaMessageImage updateImage(KafkaMessageAuth request) {
        return null;
    }

    @Override
    public KafkaMessageImage getImage(KafkaMessageAuth request) {
        String token = request.args().get(MessagePropertiesConstants.TOKEN).toString();

        if (token == null) return null;

        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of(MessagePropertiesConstants.USER_ID, userId, MessagePropertiesConstants.TOKEN, token));
    }

    @Override
    public KafkaMessageImage getAllImages(KafkaMessageAuth request) {
        String token = request.args().get(MessagePropertiesConstants.TOKEN).toString();
        if (token == null)
            return null;
        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of(MessagePropertiesConstants.USER_ID, userId, MessagePropertiesConstants.TOKEN, token));
    }

    @Override
    public KafkaMessageImage transformImage(KafkaMessageAuth request) {
        return null;
    }

    private KafkaMessageImage buildResponse(KafkaMessageAuth request, Map<String, Object> args) {
        return new KafkaMessageImage(
                request.destinationEvent(),
                args,
                request.event(),
                request.UUID()
        );
    }
}
