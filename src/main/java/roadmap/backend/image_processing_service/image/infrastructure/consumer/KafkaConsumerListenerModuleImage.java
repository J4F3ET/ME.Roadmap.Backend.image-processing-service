package roadmap.backend.image_processing_service.image.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleTransformsModuleImage;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

@Slf4j
@Service
public class KafkaConsumerListenerModuleImage {

    @Qualifier("kafkaProducerByModuleAuthModuleImage")
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    @Qualifier("kafkaProducerByModuleTransformsModuleImage")
    private final KafkaProducerByModuleTransformsModuleImage kafkaProducerByModuleTransformsModuleImage;
    private final KafkaServiceModuleImage kafkaServiceModuleImage;

    public KafkaConsumerListenerModuleImage(
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage,
            KafkaProducerByModuleTransformsModuleImage kafkaProducerByModuleTransformsModuleImage,
            KafkaServiceModuleImage kafkaServiceModuleImage
    ) {
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
        this.kafkaProducerByModuleTransformsModuleImage = kafkaProducerByModuleTransformsModuleImage;
        this.kafkaServiceModuleImage = kafkaServiceModuleImage;
    }
    private void resolveMethodTypeByModuleImage(@NonNull KafkaMessageImage request) {
        switch (request.event()) {
            case GET_ALL_IMAGES, SAVE_IMAGE, GET_IMAGE -> kafkaProducerByModuleAuthModuleImage.complete(request);
            case UPDATE_IMAGE -> kafkaServiceModuleImage.updateImage(request.args());
        }
    }
    @Nullable
    private KafkaMessageTransforms resolveMethodTypeByModuleTransform(@NonNull KafkaMessageImage request) {
        return null;
    }
    @Nullable
    private KafkaMessageAuth resolveMethodTypeByModuleAuth(@NonNull KafkaMessageImage request) {
        return null;
    }
    @Nullable
    private KafkaMessage resolveMethodType(@NonNull KafkaMessageImage request) {
        switch (request.destinationEvent()) {
            case IMAGE  -> {
                resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORMATION -> {
                return resolveMethodTypeByModuleTransform(request);
            }
            case AUTH -> {
                return resolveMethodTypeByModuleAuth(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Image,groupId = "")
    public void listen(@NonNull ConsumerRecord<String, String> record) {
        KafkaMessageImage request = KafkaMessage.convertToObject(record.value(), KafkaMessageImage.class);
        if (request == null) return;

        KafkaMessage response = resolveMethodType(request);
        if (response == null) return;

        String kafkaResponse = response.convertToJson();

        if (kafkaResponse == null) return;

        switch (request.destinationEvent()) {
            case AUTH -> kafkaProducerByModuleAuthModuleImage.send(kafkaResponse, record.key());
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleImage.send(kafkaResponse, record.key());
        }
    }
}