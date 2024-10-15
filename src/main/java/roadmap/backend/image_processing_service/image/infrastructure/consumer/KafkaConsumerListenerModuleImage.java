package roadmap.backend.image_processing_service.image.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.RequestKafkaImage;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleTransformsModuleImage;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.infrastructure.producer.KafkaProducerByModuleAuthModuleImage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class KafkaConsumerListenerModuleImage {

    @Qualifier("kafkaProducerByModuleAuthModuleImage")
    private final KafkaProducerByModuleAuthModuleImage kafkaProducerByModuleAuthModuleImage;
    @Qualifier("kafkaProducerByModuleTransformsModuleImage")
    private final KafkaProducerByModuleTransformsModuleImage kafkaProducerByModuleTransformsModuleImage;
    private final KafkaServiceModuleImage kafkaServiceModuleImage;
    private final ConcurrentHashMap<String,RequestKafkaImage> pendingRequests = new ConcurrentHashMap<>();

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
    public RequestKafkaImage jsonToObject(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, RequestKafkaImage.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    private void resolveMethodTypeByModuleImage(RequestKafkaImage request) {
        switch (request.event()) {
            case GET_ALL_IMAGES -> pendingRequests.put(request.UUID(), request);
            case SAVE_IMAGE -> kafkaServiceModuleImage.saveImage(request.args());
            case UPDATE_IMAGE -> kafkaServiceModuleImage.updateImage(request.args());
        }
    }
    private String resolveMethodTypeByModuleTransform(RequestKafkaImage request) {
        return null;
    }
    private String resolveMethodType(RequestKafkaImage request) {
        switch (request.destinationEvent()) {
            case IMAGE  -> {
                resolveMethodTypeByModuleImage(request);
            }
            case TRANSFORMATION -> {
                return resolveMethodTypeByModuleTransform(request);
            }
        }
        return null;
    }
    @KafkaListener(topics = TopicConfigProperties.TOPIC_NAME_Image,groupId = "")
    public void listen(String message) {
        RequestKafkaImage request = jsonToObject(message);
        if (request == null)
            return;
        String kafkaResponse = resolveMethodType(request);
        if (kafkaResponse == null)
            return;
        switch (request.destinationEvent()) {
            case AUTH -> kafkaProducerByModuleAuthModuleImage.send(kafkaResponse);
            case TRANSFORMATION -> kafkaProducerByModuleTransformsModuleImage.send(kafkaResponse);
        }
    }
    public CompletableFuture<RequestKafkaImage> waitForRequest(String UUID) {
        CompletableFuture<RequestKafkaImage> future = new CompletableFuture<>();
        RequestKafkaImage request = pendingRequests.remove(UUID);
        return future;
    }

}