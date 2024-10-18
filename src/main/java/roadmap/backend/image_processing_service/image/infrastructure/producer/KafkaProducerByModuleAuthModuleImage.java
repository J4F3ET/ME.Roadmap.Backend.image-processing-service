package roadmap.backend.image_processing_service.image.infrastructure.producer;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.event.request.RequestKafkaImage;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerByModuleAuthModuleImage")
public class KafkaProducerByModuleAuthModuleImage {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HashMap<String, CompletableFuture<RequestKafkaImage>> pendingRequests = new HashMap<>();

    public KafkaProducerByModuleAuthModuleImage(@Qualifier("kafkaTemplateModuleImage") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<RequestKafkaImage> send(String message, String UUID) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, UUID, message);
        CompletableFuture<RequestKafkaImage> future = new CompletableFuture<>();
        pendingRequests.put(UUID, future);
        return future;
    }
    @Async
    public void complete(@NonNull RequestKafkaImage requestKafkaImage) {
        pendingRequests.get(requestKafkaImage.UUID()).complete(requestKafkaImage);
    }
    @Async
    public void remove(@NonNull RequestKafkaImage requestKafkaImage) {
        pendingRequests.remove(requestKafkaImage.UUID());
    }
}
