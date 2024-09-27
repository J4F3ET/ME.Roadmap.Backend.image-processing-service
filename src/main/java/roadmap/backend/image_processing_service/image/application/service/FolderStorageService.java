package roadmap.backend.image_processing_service.image.application.service;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.FolderStorage;

import java.io.File;
import java.util.HashMap;

@Service
public class FolderStorageService implements FolderStorage {
    @Value("${folder.path}")
    private String folderPath;

    @Override
    public String createFolder(String folderName) {
        File folder = getFolder(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    @Override
    public boolean deleteFolder(String folderName) {
        File folder = getFolder(folderName);
        if (folder.exists()) {
            return folder.delete();
        }
        return false;
    }

    @Override
    public File getFolder(String folderName) {
        return new File(folderPath + File.separator + folderName);
    }

    @Override
    public HashMap<String,File> getAllFilesToFolder(String folderName)throws ResourceNotFoundException {
        File folder = getFolder(folderName);
        if (!folder.exists()) {
            throw new RuntimeException("Folder does not exist");
        }
        File[] files = folder.listFiles();
        if (files == null) {
            throw new RuntimeException("Folder is empty");
        }
        HashMap<String, File> filesToFolder = new HashMap<>();
        for (File file : files) {
            filesToFolder.put(file.getName(), file);
        }
        return filesToFolder;
    }
}
