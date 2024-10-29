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
    CompletableFuture<Void> resize(String uuid, Resize value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> crop(String uuid,Crop value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> rotate(String uuid,Integer value, ImageDTO image, Semaphore semaphore);
    @Async
    CompletableFuture<Void> format(String uuid,FormatImage value, ImageDTO image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> filter(String uuid,Filters value, ImageDTO image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> grayscale(String uuid,ImageDTO image,Semaphore semaphore);
    @Async
    CompletableFuture<Void> sepia(String uuid,ImageDTO image,Semaphore semaphore);
}
