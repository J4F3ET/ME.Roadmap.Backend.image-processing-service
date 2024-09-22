package roadmap.backend.image_processing_service.auth.application.adapter;

import org.springframework.stereotype.Service;


@Service
public interface AuthService {
    String register(String username, String password);
    String login(String username, String password);
    void logout(String username, String password);
}
