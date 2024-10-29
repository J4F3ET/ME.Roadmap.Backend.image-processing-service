package roadmap.backend.image_processing_service.transforms.domain.usecase;

import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.awt.image.BufferedImage;
@Service
public interface TransformationsUsecase {
    BufferedImage resize(BufferedImage image, Resize resize) throws Exception;
    BufferedImage crop(BufferedImage image, Crop crop)throws Exception;
    BufferedImage rotate(BufferedImage image, Integer angle)throws Exception;
    ImageDTO format(ImageDTO image, FormatImage format)throws Exception;
    BufferedImage grayscale(BufferedImage image)throws Exception;
    BufferedImage sepia(BufferedImage image)throws Exception;
}
