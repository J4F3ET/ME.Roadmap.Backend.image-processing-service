package roadmap.backend.image_processing_service.image.application.service;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;

import java.util.Map;
@Service
public class KafkaServiceModuleImageImpl implements KafkaServiceModuleImage {
    @Override
    public void saveImage(Map<String, Object> args) {
        System.out.println("saveImage");
    }

    @Override
    public void updateImage(Map<String, Object> args) {
        System.out.println("updateImage");
    }
}
