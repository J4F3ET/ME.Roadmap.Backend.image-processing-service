package roadmap.backend.image_processing_service.image.application.interfaces.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.image.domain.entity.ImageEntity;

import java.util.List;
import java.util.Optional;

@Service
public interface ImageRepository extends CrudRepository<ImageEntity, Integer> {
    List<ImageEntity> findByUserId(Integer userId);
    Optional<ImageEntity> findByIdAndUserId(Integer id, Integer userId);
    List<ImageEntity> findByUserId(Integer userId, Pageable pageable);
}