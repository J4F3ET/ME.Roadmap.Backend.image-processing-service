package roadmap.backend.image_processing_service.image.infrastructure.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import org.springframework.stereotype.Service;

@Service
@Qualifier("kafkaProducerByModuleTransformsModuleImage")
public class KafkaProducerByModuleTransformsModuleImage {

    @Qualifier("kafkaTemplateModuleImage")
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        // Cambiar el topic
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_ImageProcessingService, message);
    }
}
