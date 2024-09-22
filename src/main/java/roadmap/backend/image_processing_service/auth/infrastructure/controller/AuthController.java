package roadmap.backend.image_processing_service.auth.infrastructure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthService;




@Controller
@ResponseBody
@RequestMapping("/auth")
@PreAuthorize("denyAll()")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public String logout() {
        System.out.println("LLEGA LA PETICION LOGOUT");
        //authService.logout(user);
        return "logout";
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public String login(@RequestBody String username, @RequestBody String password) {
        System.out.println("LLEGA LA PETICION "+ username );
        //authService.login(user);
        return "login";
    }
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public String register(@RequestBody String username, @RequestBody String password) {
        System.out.println("LLEGA LA PETICION REGISTER");
        authService.register(username, password);
        return "register";
    }
}
