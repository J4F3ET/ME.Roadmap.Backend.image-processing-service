package roadmap.backend.image_processing_service.image.application.interfaces;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;

@Service
public interface FolderStorage {

    String createFolder(String folderName);
    boolean deleteFolder(String folderName);
    File getFolder(String folderName);
    HashMap<String, File> getAllFilesToFolder(String folderName);

}
