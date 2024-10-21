package roadmap.backend.image_processing_service.image.infrastructure.producer;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerByModuleAuthModuleImage")
public class KafkaProducerByModuleAuthModuleImage {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HashMap<String, CompletableFuture<KafkaMessageImage>> pendingRequests = new HashMap<>();

    public KafkaProducerByModuleAuthModuleImage(@Qualifier("kafkaTemplateModuleImage") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<KafkaMessageImage> send(String message, String UUID) {
        kafkaTemplate.send(TopicConfigProperties.TOPIC_NAME_Auth, UUID, message);
        CompletableFuture<KafkaMessageImage> future = new CompletableFuture<>();
        pendingRequests.put(UUID, future);
        return future;
    }
    @Async
    public void complete(@NonNull KafkaMessageImage request) {
        pendingRequests.get(request.UUID()).complete(request);
    }
    @Async
    public void remove(@NonNull KafkaMessageImage requestKafkaImage) {
        pendingRequests.remove(requestKafkaImage.UUID());
    }
}
