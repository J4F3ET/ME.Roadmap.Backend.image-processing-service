package roadmap.backend.image_processing_service.transforms.infrastructure.producer;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerTransforms")
public class KafkaProducerTransformsService implements KafkaProducerTransforms {
    final KafkaTemplate<String, String> kafkaTemplate;
    private final HashMap<String, CompletableFuture<KafkaMessageTransforms>> pendingRequests = new HashMap<>();

    public KafkaProducerTransformsService(@Qualifier("kafkaTemplateModuleTransforms") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private CompletableFuture<KafkaMessageTransforms> sendToModule(String topic,@NonNull KafkaMessage message){
        kafkaTemplate.send(topic, message.convertToJson());
        return null;
    }

    private CompletableFuture<KafkaMessageTransforms> sendToModule(String topic,String uuid, String message){
        return null;
    }

    private CompletableFuture<KafkaMessageTransforms> sendToImage(@NonNull KafkaMessageImage message) {
        return null;
    }

    @Override
    public CompletableFuture<KafkaMessageTransforms> createPromise(String uuid) {
        return null;
    }

    @Override
    public void remove(String uuid) {

    }

    @Override
    public void remove(KafkaMessageTransforms message) {

    }

    @Override
    public void complete(String uuid, KafkaMessageTransforms message) {

    }

    @Override
    public void complete(String uuid, String message) {

    }

    @Override
    public void complete(KafkaMessageTransforms message) {

    }

    @Override
    public void complete(String message) {

    }

    @Override
    public CompletableFuture<KafkaMessageTransforms> send(KafkaMessage message) {
        return null;
    }

    @Override
    public CompletableFuture<KafkaMessageTransforms> send(String key, String value) {
        return null;
    }

    @Override
    public CompletableFuture<KafkaMessageTransforms> send(String topic, KafkaMessage message) {
        return null;
    }

    @Override
    public CompletableFuture<KafkaMessageTransforms> send(String topic, String key, String value) {
        return null;
    }

    @Override
    public void sendResponse(String uuid, KafkaMessage message) {
        switch (message.destinationEvent()) {
            case IMAGE -> kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Image, uuid, message.convertToJson());
            default -> {}
        }
    }
}
