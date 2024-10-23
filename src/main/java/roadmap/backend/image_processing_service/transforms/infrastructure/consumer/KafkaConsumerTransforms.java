package roadmap.backend.image_processing_service.transforms.infrastructure.consumer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.transforms.application.event.component.KafkaEvent;
import roadmap.backend.image_processing_service.transforms.application.event.component.MessagePropertiesConstants;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.infrastructure.producer.KafkaProducerTransforms;

import java.util.Map;

@Slf4j
@Service
public class KafkaConsumerTransforms {
    private final KafkaProducerTransforms producer;

    public KafkaConsumerTransforms(
            @Qualifier("kafkaProducerTransforms") KafkaProducerTransforms producer) {
        this.producer = producer;
    }

    @Nullable
    private KafkaMessageImage resolveMethodTypeImage(KafkaMessageTransforms message) {
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
        default -> null;
        };
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Transform,groupId = "")
    public void listen(@NonNull ConsumerRecord<String, String> record) {
        KafkaMessageTransforms message = KafkaMessage.convertToObject(record.value(),KafkaMessageTransforms.class);
        if (message == null) return;
        KafkaMessage response = resolveMethodType(message);
        if (response == null) return;
        producer.sendResponse(record.key(), (KafkaMessageImage) response);
    }
//    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Transform,groupId = "")
//    public void listen(@NonNull String messageJson) {
//        System.err.println("LLEGO EN LISTEN sin UID");
//        KafkaMessageTransforms message = KafkaMessage.convertToObject(messageJson,KafkaMessageTransforms.class);
//        if (message == null) return;
//        System.err.println("message: " + message.UUID());
//        KafkaMessage response = resolveMethodType(message);
//        if (response == null) return;
//        System.err.println("response: " + response.convertToJson());
//        switch (message.destinationEvent()) {
//            case IMAGE -> kafkaProducerImage.sendResponse((KafkaMessageImage) response, message.UUID());
//            case AUTH -> kafkaProducerImage.sendResponse((KafkaMessageImage) response, message.UUID());
//        }
//    }
}
