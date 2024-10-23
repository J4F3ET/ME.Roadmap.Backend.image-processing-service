package roadmap.backend.image_processing_service.image.infrastructure.producer;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageAuth;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageTransforms;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerImage")
public class KafkaProducerImageService implements KafkaProducerImage {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HashMap<String, CompletableFuture<KafkaMessageImage>> pendingRequests = new HashMap<>();

    public KafkaProducerImageService(
            @Qualifier("kafkaTemplateModuleImage") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private CompletableFuture<KafkaMessageImage> sendToAuth(@NonNull KafkaMessageAuth message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, message.convertToJson());
        return createPromise(message.UUID());
    }
    private CompletableFuture<KafkaMessageImage> sendToAuth(String uuid, String message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, message);
        return createPromise(uuid);
    }

    private CompletableFuture<KafkaMessageImage> sendToTransforms(@NonNull KafkaMessageTransforms message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Transform, message.convertToJson());
        return createPromise(message.UUID());
    }

    private CompletableFuture<KafkaMessageImage> sendToTransforms(String uuid, String message) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Transform, message);
        return createPromise(uuid);
    }

    private CompletableFuture<KafkaMessageImage> sendToModule(String topic,@NonNull KafkaMessage message){
        kafkaTemplate.send(topic, message.convertToJson());
        return createPromise(message.UUID());
    }
    private CompletableFuture<KafkaMessageImage> sendToModule(String topic,String uuid, String message){
        kafkaTemplate.send(topic, uuid, message);
        return createPromise(uuid);
    }

    @Override
    public CompletableFuture<KafkaMessageImage> createPromise(String uuid) {
        CompletableFuture<KafkaMessageImage> future = new CompletableFuture<>();
        pendingRequests.put(uuid, future);
        return future;
    }
    @Override
    public CompletableFuture<KafkaMessageImage> send(String uuid, String message) {
        KafkaMessage kafkaMessage = KafkaMessage.convertToObject(message, KafkaMessage.class);
        if (kafkaMessage == null) return null;
        return switch (kafkaMessage.destinationEvent()) {
            case AUTH -> sendToAuth(uuid, message);
            case TRANSFORMATION -> sendToTransforms(uuid, message);
            default -> null;
        };
    }
    @Override
    public CompletableFuture<KafkaMessageImage> send(String topic,@NonNull KafkaMessage message) {
        return sendToModule(topic,message);
    }

    @Override
    public CompletableFuture<KafkaMessageImage> send(String topic, String key, String value) {
        return sendToModule(topic,key,value);
    }

    @Override
    public CompletableFuture<KafkaMessageImage> send(@NonNull KafkaMessage message) {
        return switch (message.destinationEvent()) {
            case AUTH -> sendToAuth((KafkaMessageAuth) message);
            case TRANSFORMATION ->  sendToTransforms((KafkaMessageTransforms) message);
            default -> null;
        };
    }
    @Override
    public void remove(String uuid) {
        pendingRequests.remove(uuid);
    }

    @Async
    @Override
    public void remove(@NonNull KafkaMessageImage request) {
        pendingRequests.remove(request.UUID());
    }

    @Override
    public void complete(String uuid, KafkaMessageImage message) {
        pendingRequests.get(uuid).complete(message);
    }

    @Override
    public void complete(String uuid, String message) {
        pendingRequests.get(uuid).complete(KafkaMessage.convertToObject(message, KafkaMessageImage.class));
    }
    @Async
    @Override
    public void complete(@NonNull KafkaMessageImage request) {
        pendingRequests.get(request.UUID()).complete(request);
    }
    @Override
    public void complete(String message) {
        KafkaMessageImage messageImage = KafkaMessage.convertToObject(message, KafkaMessageImage.class);
        if (messageImage == null) return;
        pendingRequests.get(messageImage.UUID()).complete(messageImage);
    }
    @Override
    public void sendResponse(String uuid, KafkaMessage message) {
        switch (message.destinationEvent()) {
            case IMAGE -> kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Image, uuid, message.convertToJson());
            default -> {}
        }
    }
}
