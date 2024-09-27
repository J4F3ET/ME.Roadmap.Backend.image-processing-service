package roadmap.backend.image_processing_service.auth.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import roadmap.backend.image_processing_service.auth.application.interfaces.apiRest.AuthResponse;
import roadmap.backend.image_processing_service.auth.application.interfaces.AuthService;
import roadmap.backend.image_processing_service.auth.application.interfaces.apiRest.AuthRequest;

@Controller
@ResponseBody
@RequestMapping("/")
@PreAuthorize("denyAll()")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated AuthRequest user) {
        return ResponseEntity.ok(this.authService.login(user.username(), user.password()));
    }
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> register(@RequestBody @Validated AuthRequest user) {
        return ResponseEntity.ok(this.authService.register(user.username(), user.password()));
    }
}
