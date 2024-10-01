package roadmap.backend.image_processing_service.image.infrastructure.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;

@Service
@Qualifier("kafkaProducerByModuleAuthModuleImage")
public class KafkaProducerByModuleAuthModuleImage {
    @Autowired
    @Qualifier("kafkaTemplateModuleImage")
    private KafkaTemplate<String, String> kafkaTemplate;
    public void send(String message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, message);
    }
}
