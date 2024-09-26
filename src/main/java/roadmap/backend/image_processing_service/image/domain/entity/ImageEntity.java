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
    private String image_name;
    private String image_path;
    private Integer userId;
    private Timestamp created_at;
    private Timestamp updated_at;
}