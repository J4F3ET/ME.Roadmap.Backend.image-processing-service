package roadmap.backend.image_processing_service.auth.application.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

public interface UserDetailServiceAdapter extends UserDetailsService {
    UserDetailsCustom loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDetailsCustom parseUser(UserEntity userEntity);
}
