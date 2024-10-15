package roadmap.backend.image_processing_service.image.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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
    @Column("image_name")
    private String imageName;
    @Column("image_path")
    private String imagePath;
    @Column("format")
    private String format;
    @Column("user_id")
    private Integer userId;
    @Column("created_at")
    private Timestamp createdAt;
    @Column("updated_at")
    private Timestamp updatedAt;
}