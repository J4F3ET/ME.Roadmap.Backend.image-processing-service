package roadmap.backend.image_processing_service.image.application.service;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.FolderStorage;
import roadmap.backend.image_processing_service.image.application.interfaces.repository.ImageStorageTemporary;
import roadmap.backend.image_processing_service.image.domain.dto.ImageDTO;

import java.io.*;
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
    @Nullable
    MultipartFile parseFileToMultipartFile(@NotNull File file) {
        String contentType = "image/" + file.getName().split("\\.")[1];
        try (InputStream inputStream = new FileInputStream(file)){
            return new MockMultipartFile(file.getName(), file.getName(),contentType,inputStream);
        } catch (IOException e) {
            System.out.println("Error reading file");
            return null;
        }

    }
    @Nullable
    private File getImageFile(String token){

        File file = this.folderStorage.getFolder(folderLabel+File.separator+token);
        if (file == null)
            return null;

        File[] files = file.listFiles();
        if (files == null)
            return null;

        return files[0];
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
    @Async
    @Override
    public void uploadImage(String token, ImageDTO imageDTO)throws RuntimeException {

        String path = this.folderStorage.createFolder(folderLabel+File.separator+token);
        if (path == null)
            throw new RuntimeException("Error uploading image, folder creation failed");

        File file = new File(path + File.separator + imageDTO.name()+"."+imageDTO.format());

        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {

            boolean result = file.createNewFile();
            if (!result)
                throw new RuntimeException("Error uploading image, file creation failed");

            fileOutputStream.write(imageDTO.image());
        } catch (Exception e) {
            throw new RuntimeException("Error in uploading: "+e.getMessage());
        }
    }


    @Override
    public MultipartFile downloadFileImage(String token) throws RuntimeException {

        File imageFile = this.getImageFile(token);

        if (imageFile == null)
            throw new RuntimeException("Error retrieving image file");

        MultipartFile multipartFile = parseFileToMultipartFile(imageFile);
        if (multipartFile == null)
            throw new RuntimeException("No se pudo convertir el archivo a MultipartFile");

        if (!this.folderStorage.deleteFolder(folderLabel+File.separator+token))
            throw new RuntimeException("No se pudo eliminar el directorio");

        return multipartFile;
    }

    public ImageDTO downloadImageDTO(String token) throws RuntimeException {

        File imageFile = this.getImageFile(token);
        if (imageFile == null)
            throw new RuntimeException("Error retrieving image file");
        String[] dataImage = imageFile.getName().split("\\.");//Primero es el nombre y luego el formato
        ImageDTO imageDTO;
        try(FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] image = new byte[(int) imageFile.length()];
            fileInputStream.read(image);
            imageDTO = new ImageDTO(dataImage[0],dataImage[1],image);
        } catch (Exception e) {
            throw new RuntimeException("Error in downloading: "+e.getMessage());
        }

        if (!this.folderStorage.deleteFolder(folderLabel+File.separator+token))
            throw new RuntimeException("No se pudo eliminar el directorio");

        return imageDTO;
    }
}
