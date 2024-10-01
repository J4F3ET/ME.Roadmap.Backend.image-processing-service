package roadmap.backend.image_processing_service.auth.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.KafkaEventByModuleImage;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.request.AuthKafkaRequest;
import roadmap.backend.image_processing_service.auth.application.interfaces.event.response.AuthKafkaResponse;
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
    public AuthKafkaRequest jsonToObject(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, AuthKafkaRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    @NonNull
    public <T> String jsonString(T object){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    @Nullable
    private AuthKafkaResponse resolveMethodTypeByModuleImage(AuthKafkaRequest request) {
        switch (request.methodType()) {
            case SAVE_IMAGE -> {
                return kafkaEventByModuleImage.saveImage(request.args()[0]);
            }
            case UPDATE_IMAGE -> {
                return kafkaEventByModuleImage.updateImage(request.args()[0]);
            }
            case GET_ALL_IMAGES -> {
                return kafkaEventByModuleImage.getAllImages(request.args()[0]);
            }
            case GET_IMAGE -> {
                return kafkaEventByModuleImage.getImage(request.args()[0]);
            }
            case TRANSFORM_IMAGE -> {
                return kafkaEventByModuleImage.transformImage(request.args()[0]);
            }
        }
        return null;
    }
    @Nullable
    private AuthKafkaResponse resolveMethodTypeByModuleTransform(AuthKafkaRequest request) {
        return null;
    }
    @Nullable
    private AuthKafkaResponse resolveMethodType(String message) {
        AuthKafkaRequest request = jsonToObject(message);
        if (request == null)
            return null;
        switch (request.methodType()) {
            case SAVE_IMAGE, UPDATE_IMAGE, GET_ALL_IMAGES, GET_IMAGE  -> {
                return resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORM_IMAGE -> {
                 return resolveMethodTypeByModuleTransform(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Auth,groupId = "")
    public void listen(String message) {
        AuthKafkaResponse result = resolveMethodType(message);
        String jsonResult = jsonString(result);
        if (result == null)
            return;
        switch (result.destinationEvent()) {
            case IMAGE -> kafkaProducerByModuleImageModuleAuth.send(jsonResult);
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleAuth.send(jsonResult);
        }
    }
}
