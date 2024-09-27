package roadmap.backend.image_processing_service.image.infrastructure.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
@Slf4j
@Service
public class KafkaConsumerListenerModuleImage {
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_ImageProcessingService,groupId = "")
    public void listen(String message) {
        log.info("Kafka consumer module image");
        System.out.println("BANDERA DE ENTRADA A LISTENER DE IMAGEN");
        System.out.println(message);
    }
}
