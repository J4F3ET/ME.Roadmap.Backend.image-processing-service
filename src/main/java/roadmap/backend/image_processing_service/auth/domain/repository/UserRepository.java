package roadmap.backend.image_processing_service.auth.domain.repository;

import org.springframework.data.repository.CrudRepository;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
}
