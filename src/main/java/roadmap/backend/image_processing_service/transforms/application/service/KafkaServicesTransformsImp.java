package roadmap.backend.image_processing_service.transforms.application.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.transforms.application.interfaces.KafkaServiceTransforms;
import roadmap.backend.image_processing_service.transforms.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.util.concurrent.CompletableFuture;
@Service
@Primary
public class KafkaServicesTransformsImp implements KafkaServiceTransforms {


    @Override
    public CompletableFuture<KafkaMessage> execute(KafkaMessageTransforms message) {
        CompletableFuture<KafkaMessage> future = new CompletableFuture<>();
        System.out.println("Executing KafkaMessageTransforms");
        System.out.println(message.image().length);
        System.out.println("Finished KafkaMessageTransforms");
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<ImageDTO> resize(Resize value, ImageDTO image) {
        return null;
    }

    @Override
    public CompletableFuture<ImageDTO> crop(Crop value, ImageDTO image) {
        return null;
    }

    @Override
    public CompletableFuture<ImageDTO> rotate(Integer value, ImageDTO image) {
        return null;
    }

    @Override
    public CompletableFuture<ImageDTO> format(FormatImage value, ImageDTO image) {
        return null;
    }

    @Override
    public CompletableFuture<ImageDTO> filter(Filters value, ImageDTO image) {
        return null;
    }

    @Override
    public CompletableFuture<byte[]> grayscale(byte[] image) {
        return null;
    }

    @Override
    public CompletableFuture<byte[]> sepia(byte[] image) {
        return null;
    }
}
