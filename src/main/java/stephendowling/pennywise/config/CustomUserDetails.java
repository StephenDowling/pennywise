package stephendowling.pennywise.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private Integer userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Additional custom fields
    private String email;

    public CustomUserDetails(Integer userId, String username, String password, 
                             Collection<? extends GrantedAuthority> authorities, 
                             String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
    }

    // Custom getter for ID
    public Integer getUserId() {
        return userId;
    }

    // Custom getter for Email
    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Add your own logic if necessary
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Add your own logic if necessary
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Add your own logic if necessary
    }

    @Override
    public boolean isEnabled() {
        return true; // Add your own logic if necessary
    }
}

