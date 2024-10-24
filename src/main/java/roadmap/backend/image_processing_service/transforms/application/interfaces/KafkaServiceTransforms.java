package roadmap.backend.image_processing_service.transforms.application.interfaces;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.transforms.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Service
public interface KafkaServiceTransforms {
    @Async
    CompletableFuture<KafkaMessage> execute(KafkaMessageTransforms message);
    @Async
    CompletableFuture<Void> resize(Resize value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> crop(Crop value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> rotate(Integer value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> format(FormatImage value, ImageDTO image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> filter(Filters value, ImageDTO image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> grayscale(byte[] image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> sepia(byte[] image,Semaphore semaphore);
}
