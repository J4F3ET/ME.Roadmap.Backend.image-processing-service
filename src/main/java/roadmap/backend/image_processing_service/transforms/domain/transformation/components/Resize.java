package roadmap.backend.image_processing_service.transforms.domain.transformation.components;

import java.io.Serializable;

public record Resize(Integer width, Integer height)implements Serializable {}
