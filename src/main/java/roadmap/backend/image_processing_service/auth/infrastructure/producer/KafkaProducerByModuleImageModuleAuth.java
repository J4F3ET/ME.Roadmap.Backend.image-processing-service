package roadmap.backend.image_processing_service.auth.infrastructure.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;

@Service
@Qualifier("kafkaProducerByModuleImageModuleAuth")
public class KafkaProducerByModuleImageModuleAuth {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerByModuleImageModuleAuth(@Qualifier("kafkaTemplateModuleAuth") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Async
    public void send(String message, String UUID) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Image, UUID, message);
    }


}
