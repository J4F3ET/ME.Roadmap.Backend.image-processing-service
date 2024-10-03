package roadmap.backend.image_processing_service.image.application.service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.event.KafkaServiceModuleImage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
@Service
public class KafkaServiceModuleImageImpl implements KafkaServiceModuleImage {

    private final ImageStorage imageStorage;
    private final ImageStorageTemporary imageStorageTemporary;

    public KafkaServiceModuleImageImpl(ImageStorage imageStorage, ImageStorageTemporary imageStorageTemporary) {
        this.imageStorage = imageStorage;
        this.imageStorageTemporary = imageStorageTemporary;
    }
    MultipartFile parseFileToMultipartFile(File file) {
         String contentType = "image/" + file.getName().split("\\.")[1];
         byte[] content = null;
         try {
             content = Files.readAllBytes(file.toPath());
         } catch (IOException e) {
             e.printStackTrace();
         }
         return new MockMultipartFile(file.getName(), file.getName(),contentType, content);
    }
    @Override
    public void saveImage(Map<String, Object> args) {
        try {
            File file = imageStorageTemporary.downloadImage(args.get("token").toString());
            MultipartFile multipartFile = parseFileToMultipartFile(file);
            imageStorage.saveImage(Integer.parseInt(args.get("userId").toString()), multipartFile);
        } catch (Exception e) {
            System.out.println("Error al guardar imagen");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateImage(Map<String, Object> args) {
        System.out.println("updateImage");
    }
}
