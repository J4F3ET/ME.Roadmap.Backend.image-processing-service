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
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.RequestKafkaImage;
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
    @Nullable
    public RequestKafkaImage jsonToObject(@NonNull String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, RequestKafkaImage.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    private void resolveMethodTypeByModuleImage(@NonNull RequestKafkaImage request) {
        switch (request.event()) {
            case GET_ALL_IMAGES, SAVE_IMAGE, GET_IMAGE -> kafkaProducerByModuleAuthModuleImage.complete(request);
            case UPDATE_IMAGE -> kafkaServiceModuleImage.updateImage(request.args());
        }
    }
    @Nullable
    private String resolveMethodTypeByModuleTransform(@NonNull RequestKafkaImage request) {
        return null;
    }
    @Nullable
    private String resolveMethodTypeByModuleAuth(@NonNull RequestKafkaImage request) {
        return null;
    }
    @Nullable
    private String resolveMethodType(@NonNull RequestKafkaImage request) {
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
        RequestKafkaImage request = jsonToObject(record.value());

        if (request == null) return;

        String kafkaResponse = resolveMethodType(request);

        if (kafkaResponse == null) return;

        switch (request.destinationEvent()) {
            case AUTH -> kafkaProducerByModuleAuthModuleImage.send(kafkaResponse, record.key());
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleImage.send(kafkaResponse, record.key());
        }
    }
}