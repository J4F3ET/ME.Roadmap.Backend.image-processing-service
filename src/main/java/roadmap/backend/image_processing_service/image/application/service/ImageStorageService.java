package roadmap.backend.image_processing_service.image.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.ImageStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
public class ImageStorageService implements ImageStorage {

    private final FolderStorage folderStorage;

    public ImageStorageService(FolderStorage folderStorage) {
        this.folderStorage = folderStorage;
    }
    @Override
    public String saveImage(String username, MultipartFile file) {
        File folder = folderStorage.getFolder(username);
        if (!folder.exists()) {
            folderStorage.createFolder(username);
        }
        String imageName = file.getOriginalFilename();
        File image = new File(folder.getAbsolutePath() + File.separator + imageName);
        try {
            file.transferTo(image);
        } catch (IOException e) {
            return null;
        }
        return image.getAbsolutePath();
    }

    @Override
    public File getImage(String username, String imageName) {
        return null;
    }

    @Override
    public void deleteImage(String username, String imageName) {

    }

    @Override
    public boolean isImageExists(String username, String imageName) {
        return false;
    }

    @Override
    public HashMap<String, File> getAllImages(String username) {
        return null;
    }
}
