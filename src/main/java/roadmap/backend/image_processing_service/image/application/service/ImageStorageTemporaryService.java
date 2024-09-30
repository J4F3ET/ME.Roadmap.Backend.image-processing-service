package roadmap.backend.image_processing_service.image.application.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import java.io.File;
@Service
public class ImageStorageTemporaryService  implements ImageStorageTemporary {

    private final String folderLabel = "image";
    private final FolderStorage folderStorage;


    public ImageStorageTemporaryService(FolderStorage folderStorage) {
        this.folderStorage = folderStorage;
    }


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
    @Override
    public File downloadImage(String token) {
        File file = this.folderStorage.getFolder(folderLabel+File.separator+token);
        if (file == null)
            return null;

        File[] files = file.listFiles();
        if (files == null)
            return null;

        File imageFile = files[0];
        if (imageFile == null)
            return null;

        this.folderStorage.deleteFolder(token);
        return imageFile;
    }
}
