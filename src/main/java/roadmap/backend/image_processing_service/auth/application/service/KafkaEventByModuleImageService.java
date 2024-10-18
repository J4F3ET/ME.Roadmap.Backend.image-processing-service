package roadmap.backend.image_processing_service.auth.application.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.KafkaEventModuleAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.component.ModuleDestionationEvent;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.request.RequestKafkaAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.ResponseKafkaByImage;

import java.util.Map;

@Service
public class KafkaEventByModuleImageService implements KafkaEventByModuleImage {

    private final JwtUtils jwtUtils;

    public KafkaEventByModuleImageService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    public ResponseKafkaByImage saveImage(RequestKafkaAuth request) {
        String token = request.args().get("token").toString();

        if (token == null) return null;

        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of("user_id", userId,"token", token));
    }

    @Override
    public ResponseKafkaByImage updateImage(RequestKafkaAuth request) {
        return null;
    }

    @Override
    public ResponseKafkaByImage getImage(RequestKafkaAuth request) {
        String token = request.args().get("token").toString();

        if (token == null) return null;

        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of("user_id", userId,"token", token));
    }

    @Override
    public ResponseKafkaByImage getAllImages(RequestKafkaAuth request) {
        String token = request.args().get("token").toString();
        if (token == null)
            return null;
        Integer userId = jwtUtils.extractId(token);
        return buildResponse(request, Map.of("user_id", userId,"token", token));
    }

    @Override
    public ResponseKafkaByImage transformImage(RequestKafkaAuth request) {
        return null;
    }

    private ResponseKafkaByImage buildResponse(RequestKafkaAuth request, Map<String, Object> args) {
        return new ResponseKafkaByImage(
                request.destinationEvent(),
                args,
                request.event(),
                request.UUID()
        );
    }
}
