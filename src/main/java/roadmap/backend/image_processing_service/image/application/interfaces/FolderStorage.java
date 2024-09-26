package roadmap.backend.image_processing_service.image.application.interfaces;

import roadmap.backend.image_processing_service.image.domain.entity.ImageDTO;

import java.util.HashMap;

public interface FolderStorage {

    String createFolder(String folderName);
    boolean deleteFolder(String folderName);
    HashMap<Integer, ImageDTO> getAllFilesToFolder(String folderName);

}
