package roadmap.backend.image_processing_service.transforms.infrastructure.consumer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import roadmap.backend.image_processing_service.transforms.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.transforms.application.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.transforms.application.event.component.MessagePropertiesConstants;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.infrastructure.producer.KafkaProducerByModuleImageModuleTransforms;

import java.util.Map;

@Slf4j
public class KafkaConsumerListenerModuleTransforms {
    @Qualifier("kafkaProducerByModuleImageModuleTransforms")
    private final KafkaProducerByModuleImageModuleTransforms kafkaProducerImage;

    public KafkaConsumerListenerModuleTransforms(KafkaProducerByModuleImageModuleTransforms kafkaProducerImage) {
        this.kafkaProducerImage = kafkaProducerImage;
    }

    @Nullable
    private KafkaMessageImage resolveMethodTypeImage(KafkaMessageTransforms message) {
        System.out.println("resolveMethodTypeImage: " + message.convertToJson());
        Map<String, Object> args = Map.of(
                MessagePropertiesConstants.NAME, message.name(),
                MessagePropertiesConstants.FORMAT, FormatImage.BMP,
                MessagePropertiesConstants.IMAGE, message.image()
        );
        return new KafkaMessageImage(
                message.destinationEvent(),
                KafkaEvent.TRANSFORM_IMAGE,
                args,
                message.UUID()
        );
    }
    @Nullable
    private KafkaMessage resolveMethodType(KafkaMessageTransforms message) {
        return switch (message.destinationEvent()) {
            case IMAGE -> resolveMethodTypeImage(message);
            case AUTH -> null;
        default -> null;
        };
    }
    @Async
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Transform,groupId = "")
    public void listen(@NonNull ConsumerRecord<String, KafkaMessageTransforms> record) {
        System.err.println("ESCUCHANDO DESDE TRANSFORMER ");
        KafkaMessageTransforms message = record.value();
        if (message == null) return;
        KafkaMessage response = resolveMethodType(message);
        if (response == null) return;
        switch (message.destinationEvent()) {
            case IMAGE -> kafkaProducerImage.sendResponse((KafkaMessageImage) response, record.key());
            case AUTH -> kafkaProducerImage.sendResponse((KafkaMessageImage) response, record.key());
        }
    }
}
