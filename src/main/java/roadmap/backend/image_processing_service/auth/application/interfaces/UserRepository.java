package roadmap.backend.image_processing_service.auth.application.interfaces;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

import java.util.Optional;
@Service
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
}
