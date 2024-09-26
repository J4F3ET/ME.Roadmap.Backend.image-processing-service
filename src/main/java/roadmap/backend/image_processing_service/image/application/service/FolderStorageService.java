package roadmap.backend.image_processing_service.image.application.service;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.application.interfaces.FolderStorage;
import roadmap.backend.image_processing_service.image.domain.entity.ImageDTO;

import java.util.HashMap;

@Service
public class FolderStorageService implements FolderStorage {
    @Override
    public String createFolder(String folderName) {
        return "";
    }

    @Override
    public boolean deleteFolder(String folderName) {
        return false;
    }

    @Override
    public HashMap<Integer, ImageDTO> getAllFilesToFolder(String folderName) {
        return null;
    }
}
