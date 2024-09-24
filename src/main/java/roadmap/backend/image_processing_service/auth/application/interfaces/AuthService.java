package roadmap.backend.image_processing_service.auth.application.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public interface AuthService {
    final UserDetailsService userDetailsService = null;
    public AuthResponse register(String username, String password);
    public AuthResponse login(String username, String password);
}
