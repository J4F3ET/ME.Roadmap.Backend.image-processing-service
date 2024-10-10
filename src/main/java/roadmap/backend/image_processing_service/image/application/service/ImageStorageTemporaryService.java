package roadmap.backend.image_processing_service.image.application.service;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class ImageStorageTemporaryService  implements ImageStorageTemporary {

    private final String folderLabel = "image";
    private final FolderStorage folderStorage;

    void init() {
        folderStorage.createFolder(folderLabel);
    }

    public ImageStorageTemporaryService(FolderStorage folderStorage) {
        this.folderStorage = folderStorage;
    }

    @Async
    @Override
    public void uploadImage(String token, MultipartFile image)throws RuntimeException {
        String path = this.folderStorage.createFolder(folderLabel+File.separator+token);
        if (path == null)
            throw new RuntimeException("Error uploading image, folder creation failed");
        File file = new File(path + File.separator + image.getOriginalFilename());
        try {
            image.transferTo(file);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image, file transfer failed");
        }
    }
    MultipartFile parseFileToMultipartFile(File file) {
        String contentType = "image/" + file.getName().split("\\.")[1];
        try (InputStream inputStream = new FileInputStream(file)){
            return new MockMultipartFile(file.getName(), file.getName(),contentType,inputStream);
        } catch (IOException e) {
            System.out.println("Error reading file");
            return null;
        }

    }
    @Override
    public MultipartFile downloadImage(String token)throws RuntimeException {

        File file = this.folderStorage.getFolder(folderLabel+File.separator+token);
        if (file == null)
            throw new RuntimeException("No se pudo encontrar el fichero");

        File[] files = file.listFiles();
        if (files == null)
            throw new RuntimeException("No se pudo encontrar archivos en el directorio");

        File imageFile = files[0];
        if (imageFile == null)
            throw new RuntimeException("No se pudo encontrar el archivo");

        MultipartFile multipartFile = parseFileToMultipartFile(imageFile);
        if (multipartFile == null)
            throw new RuntimeException("No se pudo convertir el archivo a MultipartFile");

        if (!this.folderStorage.deleteFolder(folderLabel+File.separator+token))
            throw new RuntimeException("No se pudo eliminar el directorio");

        return multipartFile;
    }
}
