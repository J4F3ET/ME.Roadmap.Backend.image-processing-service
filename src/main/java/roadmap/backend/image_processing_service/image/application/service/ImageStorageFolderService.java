package roadmap.backend.image_processing_service.image.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
public class ImageStorageFolderService implements ImageStorage {

    private final FolderStorage folderStorage;

    public ImageStorageFolderService(FolderStorage folderStorage) {
        this.folderStorage = folderStorage;
    }

    @Override
    public String saveImage(Integer Id, MultipartFile file) {
        File folder = folderStorage.getFolder(Id.toString());
        if (!folder.exists()) {
            folderStorage.createFolder(Id.toString());
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
    public String getImageUrl(Integer Id) {
        return "";
    }

    @Override
    public File getImageFile(Integer Id) {
        return null;
    }

    @Override
    public String updateImage(Integer id, MultipartFile file) {
        return "";
    }

    @Override
    public void deleteImage(Integer Id, String imageName) {

    }

    @Override
    public boolean isImageExists(Integer Id, String imageName) {
        return false;
    }

    @Override
    public HashMap<String, File> getAllImages(Integer Id) {
        return null;
    }
}
