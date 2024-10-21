package roadmap.backend.image_processing_service.image.infrastructure.producer;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageTransforms;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerByModuleTransformsModuleImage")
public class KafkaProducerByModuleTransformsModuleImage {

    private final KafkaTemplate<String, KafkaMessageTransforms> kafkaTemplate;
    private final HashMap<String, CompletableFuture<KafkaMessageImage>> pendingRequests = new HashMap<>();

    public KafkaProducerByModuleTransformsModuleImage(
            @Qualifier("kafkaTemplateByTransformsModuleImage") KafkaTemplate<String, KafkaMessageTransforms> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Async
    public CompletableFuture<KafkaMessageImage> send(KafkaMessageTransforms message, String UUID) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Transform, UUID, message);
        CompletableFuture<KafkaMessageImage> future = new CompletableFuture<>();
        pendingRequests.put(UUID, future);
        return future;
    }
    @Async
    public CompletableFuture<KafkaMessageImage> send(String message, String UUID) {
        KafkaMessageTransforms messageRequest = KafkaMessage.convertToObject(message, KafkaMessageTransforms.class);
        if (messageRequest == null) {
            return CompletableFuture.completedFuture(null);
        }
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Transform, UUID, messageRequest);
        CompletableFuture<KafkaMessageImage> future = new CompletableFuture<>();
        pendingRequests.put(UUID, future);
        return future;
    }
    @Async
    public void complete(@NonNull KafkaMessageImage request) {
        pendingRequests.get(request.UUID()).complete(request);
    }
    @Async
    public void remove(@NonNull KafkaMessageImage request) {
        pendingRequests.remove(request.UUID());
    }
}
