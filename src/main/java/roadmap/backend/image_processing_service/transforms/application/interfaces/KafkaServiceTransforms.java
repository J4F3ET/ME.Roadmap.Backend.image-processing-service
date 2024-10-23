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
@Service
public interface KafkaServiceTransforms {
    @Async
    CompletableFuture<KafkaMessage> execute(KafkaMessageTransforms message);
    @Async
    CompletableFuture<ImageDTO> resize(Resize value, ImageDTO image);
    @Async
    CompletableFuture<ImageDTO> crop(Crop value, ImageDTO image);
    @Async
    CompletableFuture<ImageDTO> rotate(Integer value, ImageDTO image);
    @Async
    CompletableFuture<ImageDTO> format(FormatImage value, ImageDTO image);
    @Async
    CompletableFuture<ImageDTO> filter(Filters value, ImageDTO image);
    @Async
    CompletableFuture<byte[]> grayscale(byte[] image);
    @Async
    CompletableFuture<byte[]> sepia(byte[] image);
}
