package roadmap.backend.image_processing_service.image.infrastructure.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.image.application.interfaces.event.message.implement.KafkaMessageImage;

import java.util.concurrent.CompletableFuture;
@Service
public interface KafkaProducerImage {
    @Async
    CompletableFuture<KafkaMessageImage> createPromise(String uuid);
    @Async
    void remove(String uuid);
    @Async
    void remove(KafkaMessageImage message);
    @Async
    void complete(String uuid, KafkaMessageImage message);
    @Async
    void complete(String uuid, String message);
    @Async
    void complete(KafkaMessageImage message);
    @Async
    void complete(String message);
    @Async
    CompletableFuture<KafkaMessageImage> send(KafkaMessage message);
    @Async
    CompletableFuture<KafkaMessageImage> send(String key, String value);
    @Async
    CompletableFuture<KafkaMessageImage> send(String topic, KafkaMessage message);
    @Async
    CompletableFuture<KafkaMessageImage> send(String topic, String key, String value);
    @Async
    void sendResponse(String uuid,KafkaMessage message);
}
