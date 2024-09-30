package roadmap.backend.image_processing_service.image.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("image")
public class ImageEntity {
    @Id
    private Integer id;
    private String imageName;
    private String imagePath;
    private String format;
    private Integer userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}