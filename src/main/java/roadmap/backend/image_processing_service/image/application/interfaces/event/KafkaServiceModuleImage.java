package roadmap.backend.image_processing_service.image.application.interfaces.event;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface KafkaServiceModuleImage {
    void saveImage(Map<String, Object> args);
    void updateImage(Map<String, Object> args);
}
