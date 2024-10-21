package roadmap.backend.image_processing_service.transforms.domain.transformation.components;

import java.io.Serializable;

public record Filters(
        Boolean grayscale,
        Boolean sepia
) implements Serializable { }
