package roadmap.backend.image_processing_service.transforms.domain.transformation;

import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.io.Serial;
import java.io.Serializable;

public class Transformation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    final private Resize resize;
    final private Crop crop;
    final private Integer rotate;
    final private FormatImage format;
    final private Filters filters;

    public Transformation(
            Resize resize,
            Crop crop,
            Integer rotate,
            FormatImage format,
            Filters filters) {
        this.resize = resize;
        this.crop = crop;
        this.rotate = rotate;
        this.format = format;
        this.filters = filters;
    }
}
