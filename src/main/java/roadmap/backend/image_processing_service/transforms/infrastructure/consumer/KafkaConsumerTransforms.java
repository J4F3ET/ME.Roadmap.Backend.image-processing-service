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
import roadmap.backend.image_processing_service.transforms.application.interfaces.KafkaServiceTransforms;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.infrastructure.producer.KafkaProducerTransforms;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KafkaConsumerTransforms {
    private final KafkaProducerTransforms producer;
    private final KafkaServiceTransforms transformServices;
    public KafkaConsumerTransforms(
            @Qualifier("kafkaProducerTransforms") KafkaProducerTransforms producer,
             KafkaServiceTransforms transformServices
    ) {
        this.producer = producer;
        this.transformServices = transformServices;
    }

    @Nullable
    private KafkaMessageImage resolveMethodTypeImage(KafkaMessageTransforms message) {
        CompletableFuture<KafkaMessage> future = transformServices.execute(message);
        KafkaMessage kafkaMessage;
        try {
            kafkaMessage = future.get(3, TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return (KafkaMessageImage) kafkaMessage;
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
        producer.sendResponse(record.key(),response);
    }
}
