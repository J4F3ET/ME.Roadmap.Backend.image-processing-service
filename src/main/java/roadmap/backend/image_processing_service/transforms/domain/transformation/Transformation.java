package roadmap.backend.image_processing_service.transforms.domain.transformation;

import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.io.Serial;
import java.io.Serializable;
public record Transformation(
        Resize resize,
        Crop crop,
        Integer rotate,
        FormatImage format,
        Filters filters
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
