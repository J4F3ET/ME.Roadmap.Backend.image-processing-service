package roadmap.backend.image_processing_service.auth.application.adapter;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

public interface UserDetailServiceAdapter extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDetails parseUser(UserEntity userEntity);
}
