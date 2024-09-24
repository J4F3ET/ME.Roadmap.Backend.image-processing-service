package roadmap.backend.image_processing_service.auth.application.adapter;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public final class UserDetailsCustom implements UserDetails{
    @Getter
    private final Integer id;
    private final String username;
    private final String password;
    private final boolean isEnabled;
    private final boolean isAccountNonExpired;
    private final boolean isCredentialsNonExpired;
    private final boolean isAccountNonLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsCustom(
            Integer id,
            String username,
            String password,
            boolean isEnabled,
            boolean isAccountNonExpired,
            boolean isCredentialsNonExpired,
            boolean isAccountNonLocked,
            Collection<? extends GrantedAuthority> authorities

    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}

