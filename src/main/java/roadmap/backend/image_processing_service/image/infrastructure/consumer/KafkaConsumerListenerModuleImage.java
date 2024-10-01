package roadmap.backend.image_processing_service.image.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.Utils;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.ImageKafkaRequest;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleTransformsModuleImage;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.event.response.ImageKafkaResponse;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

@Slf4j
@Service
public class KafkaConsumerListenerModuleImage {

    private final ImageStorageTemporary imageStorageTemporary;
    @Qualifier("kafkaProducerByModuleAuthModuleImage")
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    @Qualifier("kafkaProducerByModuleTransformsModuleImage")
    private final KafkaProducerByModuleTransformsModuleImage kafkaProducerByModuleTransformsModuleImage;
    private final KafkaServiceModuleImage kafkaServiceModuleImage;
    private final Utils utils;

    public KafkaConsumerListenerModuleImage(
            ImageStorageTemporary imageStorageTemporary,
            KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage,
            KafkaProducerByModuleTransformsModuleImage kafkaProducerByModuleTransformsModuleImage,
            KafkaServiceModuleImage kafkaServiceModuleImage, Utils utils
    ) {
        this.imageStorageTemporary = imageStorageTemporary;
        this.kafkaProducerByModuleAuthModuleImage = kafkaProducerByModuleAuthModuleImage;
        this.kafkaProducerByModuleTransformsModuleImage = kafkaProducerByModuleTransformsModuleImage;
        this.kafkaServiceModuleImage = kafkaServiceModuleImage;
        this.utils = utils;
    }
    @Nullable
    public ImageKafkaRequest jsonToObject(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, ImageKafkaRequest.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error jsonToObject" + e);
            return null;
        }
    }
    private void resolveMethodTypeByModuleImage(ImageKafkaRequest request) {
        System.out.println("Resolve method type by module image");
        switch (request.event()) {
            case SAVE_IMAGE -> kafkaServiceModuleImage.saveImage(request.args());
            case UPDATE_IMAGE -> kafkaServiceModuleImage.updateImage(request.args());
        }
    }
    private ImageKafkaResponse resolveMethodTypeByModuleTransform(ImageKafkaRequest request) {
        return null;
    }
    private ImageKafkaResponse resolveMethodType(String message) {
        System.out.println("Resolve method type");
        ImageKafkaRequest request = jsonToObject(message);
        System.out.println("Message: " + message);
        System.out.println("Request: " + request);
        if (request == null)
            return null;

        switch (request.event()) {
            case SAVE_IMAGE, UPDATE_IMAGE, GET_ALL_IMAGES, GET_IMAGE  -> {
                resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORM_IMAGE -> {
                return resolveMethodTypeByModuleTransform(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Image,groupId = "")
    public void listen(String message) {
        System.out.println("Listen");
        ImageKafkaResponse kafkaResponse = resolveMethodType(message);
        String jsonMessage = utils.converterObjectToStringJson(kafkaResponse);
        if (kafkaResponse == null)
            return;
        switch (kafkaResponse.destinationEvent()) {
            case AUTH -> kafkaProducerByModuleAuthModuleImage.send(jsonMessage);
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleImage.send(jsonMessage);
        }
    }
}