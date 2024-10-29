package roadmap.backend.image_processing_service.transforms.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDTO{
    private String name;
    private String format;
    private byte[] image;
}