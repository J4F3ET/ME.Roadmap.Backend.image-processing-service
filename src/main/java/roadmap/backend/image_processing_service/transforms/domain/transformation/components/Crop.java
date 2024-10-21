package roadmap.backend.image_processing_service.transforms.domain.transformation.components;

import java.io.Serializable;

public record Crop(
        Integer width,
        Integer height,
        Integer x,
        Integer y
) implements Serializable {}
