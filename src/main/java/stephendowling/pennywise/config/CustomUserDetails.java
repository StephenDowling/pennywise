package stephendowling.pennywise.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails { //UserDetails is core interface from Spring Security

    private Integer userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities; //used to store ROLE_ADMIN and ROLE_USER
    private String email;

    //constructor 
    public CustomUserDetails(Integer userId, String username, String password, Collection<? extends GrantedAuthority> authorities, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
    }

    //custom getter for ID
    public Integer getUserId() {
        return userId;
    }

    //custom getter for email
    public String getEmail() {
        return email;
    }

    //default methods to override if needed 
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }
}

