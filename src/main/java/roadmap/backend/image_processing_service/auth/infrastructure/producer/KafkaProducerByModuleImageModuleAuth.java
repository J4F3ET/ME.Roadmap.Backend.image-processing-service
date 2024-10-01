package roadmap.backend.image_processing_service.auth.infrastructure.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;

@Service
@Qualifier("kafkaProducerByModuleImageModuleAuth")
public class KafkaProducerByModuleImageModuleAuth {
    @Autowired
    @Qualifier("kafkaTemplateModuleAuth")
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        System.out.println("Sending message a image desde el auth");
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Image, message);
    }


}
