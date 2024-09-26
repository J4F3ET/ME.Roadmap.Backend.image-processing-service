package roadmap.backend.image_processing_service.image.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import roadmap.backend.image_processing_service.image.application.config.topic.TopicConfigProperties;

public class KafkaConsumerListener {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerListener.class);
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME,groupId = "")
    public void listen() {
        logger.info("Kafka consumer listener");
    }
}
