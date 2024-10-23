package roadmap.backend.image_processing_service.transforms.infrastructure.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;

import java.util.concurrent.CompletableFuture;
@Service
public interface KafkaProducerTransforms {
    @Async
    CompletableFuture<KafkaMessageTransforms> createPromise(String uuid);
    @Async
    void remove(String uuid);
    @Async
    void remove(KafkaMessageTransforms message);
    @Async
    void complete(String uuid, KafkaMessageTransforms message);
    @Async
    void complete(String uuid, String message);
    @Async
    void complete(KafkaMessageTransforms message);
    @Async
    void complete(String message);
    @Async
    CompletableFuture<KafkaMessageTransforms> send(KafkaMessage message);
    @Async
    CompletableFuture<KafkaMessageTransforms> send(String key, String value);
    @Async
    CompletableFuture<KafkaMessageTransforms> send(String topic, KafkaMessage message);
    @Async
    CompletableFuture<KafkaMessageTransforms> send(String topic, String key, String value);
    @Async
    void sendResponse(String uuid,KafkaMessage message);
}
