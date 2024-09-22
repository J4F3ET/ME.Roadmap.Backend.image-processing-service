package roadmap.backend.image_processing_service.auth.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class UserEntity {
    @Id
    private Integer id;
    private String username;
    private String password;
    private boolean isEnabled;
    private boolean isAccountNoExpired;
    private boolean isAccountNoLocked;
    private boolean isCredentialsNoExpired;
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.isEnabled = true;
        this.isAccountNoExpired = true;
        this.isAccountNoLocked = true;
        this.isCredentialsNoExpired = true;
    }
}
