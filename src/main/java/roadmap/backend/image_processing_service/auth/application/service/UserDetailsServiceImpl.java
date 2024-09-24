package roadmap.backend.image_processing_service.auth.application.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.adapter.UserDetailServiceAdapter;
import roadmap.backend.image_processing_service.auth.application.adapter.UserRepository;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

import java.util.Collections;
@Service
public class UserDetailsServiceImpl implements UserDetailServiceAdapter {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        return parseUser(userEntity, authority);
    }
    @Override
    public UserDetails parseUser(UserEntity userEntity){
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isAccountNoLocked(),
                userEntity.isCredentialsNoExpired(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    private UserDetails parseUser(UserEntity userEntity,SimpleGrantedAuthority authority){
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isAccountNoLocked(),
                userEntity.isCredentialsNoExpired(),
                Collections.singleton(authority)
        );
    }
}

