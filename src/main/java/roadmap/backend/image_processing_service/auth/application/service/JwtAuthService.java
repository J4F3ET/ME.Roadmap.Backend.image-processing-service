package roadmap.backend.image_processing_service.auth.application.service;

import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthResponse;
import roadmap.backend.image_processing_service.auth.application.adapter.AuthService;
import roadmap.backend.image_processing_service.auth.application.adapter.UserDetailServiceAdapter;
import roadmap.backend.image_processing_service.auth.application.adapter.UserRepository;
import roadmap.backend.image_processing_service.auth.domain.entity.UserEntity;

@Primary
@Service
public class JwtAuthService implements AuthService {
    private final UserDetailServiceAdapter userDetailsService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public JwtAuthService(
            UserDetailServiceAdapter userDetailsService,
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ){
        this.userDetailsService = userDetailsService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse register(String username, String password) {
        if (repository.findByUsername(username).isPresent()) {
            throw new UsernameNotFoundException("User already exists");
        }

        final UserEntity newUser = new UserEntity(username, passwordEncoder.encode(password));
        repository.save(newUser);

        return new AuthResponse(
                username,
                this.generateSession(userDetailsService.parseUser(newUser),password)
        );
    }
    @Override
    public AuthResponse login(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        verifyUserDetails(userDetails);
        return new AuthResponse(username, this.generateSession(userDetails,password));
    }

    @NonNull
    private Authentication authenticate(@NonNull UserDetails userDetails, String password) {

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
    @NonNull
    private String generateSession(@NonNull UserDetails userDetails,String password) {
        Authentication authentication = this.authenticate(userDetails,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateToken(userDetails);
    }
    private void verifyUserDetails(UserDetails userDetails) {
        if(userDetails == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!userDetails.isAccountNonExpired()) {
            throw new UsernameNotFoundException("User account has expired");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new UsernameNotFoundException("User account is locked");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new UsernameNotFoundException("User credentials have expired");
        }
        if (!userDetails.isEnabled()) {
            throw new UsernameNotFoundException("User is disabled");
        }
    }
}
