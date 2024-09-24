package roadmap.backend.image_processing_service.auth.infrastructure.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthResponse;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthService;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthRequest;

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
