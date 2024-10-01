package roadmap.backend.image_processing_service.auth.infrastructure.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;

@Service
@Qualifier("kafkaProducerByModuleTransformsModuleAuth")
public class KafkaProducerByModuleTransformsModuleAuth {
    @Autowired
    @Qualifier("kafkaTemplateModuleAuth")
    private KafkaTemplate<String, String> kafkaTemplate;
    // CAMBIARLE EL NOMBRE DEL TOPIC CUANDO EXITA
    public void send(String message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Image, message);
    }

}
