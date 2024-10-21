package roadmap.backend.image_processing_service.transforms.infrastructure.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.config.kafka.topic.TopicConfigProperties;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@Qualifier("kafkaProducerByModuleImageModuleTransforms")
public class KafkaProducerByModuleImageModuleTransforms {
    private final KafkaTemplate<String, String> kafkaTemplateString;
    private final HashMap<String, CompletableFuture<KafkaMessageTransforms>> pendingRequests = new HashMap<>();


    public KafkaProducerByModuleImageModuleTransforms(
            @Qualifier("kafkaTemplateStringModuleTransforms") KafkaTemplate<String, String> kafkaTemplateString) {
        this.kafkaTemplateString = kafkaTemplateString;
    }

    @Async
    public void sendResponse(KafkaMessageImage request, String key) {
        String jsonRequest = request.convertToJson();
        if(jsonRequest == null) return;
        kafkaTemplateString.send(TopicConfigProperties.TOPIC_NAME_Image, key, jsonRequest);

    }
    @Async
    public CompletableFuture<KafkaMessageTransforms> send(KafkaMessageTransforms message, String UUID) {
        kafkaTemplateString.send(UUID, message.convertToJson());
        CompletableFuture<KafkaMessageTransforms> future = new CompletableFuture<>();
        pendingRequests.put(UUID, future);
        return future;
    }
    public void complete(KafkaMessageTransforms request) {
        pendingRequests.get(request.UUID()).complete(request);
    }

    public void remove(String key) {
        pendingRequests.remove(key);
    }
}
