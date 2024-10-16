package roadmap.backend.image_processing_service.image.application.service;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
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
import java.nio.file.Paths;

import static java.io.File.separator;

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

        File file = this.folderStorage.getFolder(folderLabel+ separator+token);
        if (file == null)
            return null;

        File[] files = file.listFiles();
        if (files == null)
            return null;

        return files[0];
    }


    @Async
    @Override
    public void uploadImage(@NonNull String token,@NonNull MultipartFile image) throws RuntimeException, IOException {
        String path = this.folderStorage.createFolder(folderLabel+ separator+token);
        image.transferTo(new File(path + separator + image.getOriginalFilename()));
    }
    @Async
    @Override
    public void uploadImage(@NonNull String token,@NonNull ImageDTO imageDTO)throws RuntimeException {

        String path = this.folderStorage.createFolder(folderLabel+separator+token);
        File file = new File(path + separator + imageDTO.name()+"."+imageDTO.format());

        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(imageDTO.image());
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image, file transfer failed");
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

        if (!this.folderStorage.deleteFolder(folderLabel+ separator+token))
            throw new RuntimeException("No se pudo eliminar el directorio");

        return multipartFile;
    }
    @Nullable
    public ImageDTO downloadImageDTO(String token)   {

        File imageFile = this.getImageFile(token);

        if (imageFile == null)return null;

        String[] dataImage = imageFile.getName().split("\\.");
        ImageDTO imageDTO;

        try(FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] image = new byte[(int) imageFile.length()];
            fileInputStream.read(image);
            imageDTO = new ImageDTO(dataImage[0],dataImage[1],image);
        } catch (Exception e) {
            System.out.println("Error in downloading: "+e.getMessage());
            return null;
        }

        if (!this.folderStorage.deleteFolder(folderLabel+ separator+token)) return null;

        return imageDTO;
    }
}
