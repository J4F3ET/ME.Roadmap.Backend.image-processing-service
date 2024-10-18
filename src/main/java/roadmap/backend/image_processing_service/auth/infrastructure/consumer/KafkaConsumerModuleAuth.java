package roadmap.backend.image_processing_service.auth.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.request.RequestKafkaAuth;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.ResponseKafkaByImage;
import roadmap.backend.image_processing_service.auth.infrastructure.producer.KafkaProducerByModuleImageModuleAuth;
import roadmap.backend.image_processing_service.auth.infrastructure.producer.KafkaProducerByModuleTransformsModuleAuth;

@Slf4j
@Service
public class KafkaConsumerModuleAuth {
    private final KafkaEventByModuleImage kafkaEventByModuleImage;
    @Qualifier("kafkaProducerByModuleImageModuleAuth")
    private final KafkaProducerByModuleImageModuleAuth kafkaProducerByModuleImageModuleAuth;
    @Qualifier("kafkaProducerByModuleTransformsModuleAuth")
    private final KafkaProducerByModuleTransformsModuleAuth kafkaProducerByModuleTransformsModuleAuth;
    public KafkaConsumerModuleAuth(
            KafkaEventByModuleImage kafkaEventByModuleImage,
            KafkaProducerByModuleImageModuleAuth kafkaProducerByModuleImageModuleAuth,
            KafkaProducerByModuleTransformsModuleAuth kafkaProducerByModuleTransformsModuleAuth
    ){
        this.kafkaEventByModuleImage = kafkaEventByModuleImage;
        this.kafkaProducerByModuleImageModuleAuth = kafkaProducerByModuleImageModuleAuth;
        this.kafkaProducerByModuleTransformsModuleAuth = kafkaProducerByModuleTransformsModuleAuth;
    }
    @Nullable
    public RequestKafkaAuth jsonToObject(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, RequestKafkaAuth.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    @Nullable
    public <T> String jsonString(T object){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    @Nullable
    private ResponseKafkaByImage resolveMethodTypeByModuleImage(RequestKafkaAuth request) {
        switch (request.event()) {
            case SAVE_IMAGE -> {
                return kafkaEventByModuleImage.saveImage(request);
            }
            case UPDATE_IMAGE -> {
                return kafkaEventByModuleImage.updateImage(request);
            }
            case GET_ALL_IMAGES -> {
                return kafkaEventByModuleImage.getAllImages(request);
            }
            case GET_IMAGE -> {
                return kafkaEventByModuleImage.getImage(request);
            }
            case TRANSFORM_IMAGE -> {
                return kafkaEventByModuleImage.transformImage(request);
            }
        }
        return null;
    }
    @Nullable
    private ResponseKafkaByImage resolveMethodTypeByModuleTransform(RequestKafkaAuth request) {
        return null;
    }
    @Nullable
    private ResponseKafkaByImage resolveMethodType(String message) {
        RequestKafkaAuth request = jsonToObject(message);
        if (request == null)
            return null;
        switch (request.destinationEvent()) {
            case IMAGE  -> {
                return resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORMATION -> {
                 return resolveMethodTypeByModuleTransform(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Auth,groupId = "")
    public void listen(@NonNull ConsumerRecord<String, String> record) {
        ResponseKafkaByImage result = resolveMethodType(record.value());
        if (result == null) return;
        String jsonResult = jsonString(result);
        switch (result.destinationEvent()) {
            case IMAGE -> kafkaProducerByModuleImageModuleAuth.send(jsonResult, record.key());
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleAuth.send(jsonResult);
        }
    }
}
