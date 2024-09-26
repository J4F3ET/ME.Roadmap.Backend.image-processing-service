package roadmap.backend.image_processing_service.image.domain.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO extends ImageEntity implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private byte[] image;
}
