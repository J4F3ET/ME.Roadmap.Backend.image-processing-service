package roadmap.backend.image_processing_service.image.application.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaServiceModuleImageImpl implements KafkaServiceModuleImage {

    private final ImageStorage imageStorage;
    private final ImageStorageTemporary imageStorageTemporary;

    public KafkaServiceModuleImageImpl(ImageStorage imageStorage, ImageStorageTemporary imageStorageTemporary) {
        this.imageStorage = imageStorage;
        this.imageStorageTemporary = imageStorageTemporary;
    }
    @Transactional
    @Override
    public void saveImage(Map<String, Object> args) {
        ImageDTO imageDTO = imageStorageTemporary.downloadImageDTO(args.get("token").toString());
        if (imageDTO == null || imageDTO.image().length == 0)
            return;

        CompletableFuture<String> result= this.imageStorage.saveImage(
                Integer.parseInt(args.get("user_id").toString()),
                imageDTO
        );
        result.thenAccept(System.out::println);
    }

    @Override
    public void updateImage(Map<String, Object> args) {
        System.out.println("updateImage");
    }
}
