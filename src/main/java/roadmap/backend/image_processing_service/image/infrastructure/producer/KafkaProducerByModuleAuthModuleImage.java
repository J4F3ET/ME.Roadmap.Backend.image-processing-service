package roadmap.backend.image_processing_service.image.infrastructure.producer;

import org.apache.qpid.proton.codec.security.SaslOutcomeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;

@Service
@Qualifier("kafkaProducerByModuleAuthModuleImage")
public class KafkaProducerByModuleAuthModuleImage {
    @Autowired
    @Qualifier("kafkaTemplateModuleImage")
    private KafkaTemplate<String, String> kafkaTemplate;
    @Async
    public void send(String message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, message);
    }
    @Async
    public void send(String message, String UUID) {
        System.out.println("Sending message with UUID: " + UUID);
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, UUID, message);
    }
}
