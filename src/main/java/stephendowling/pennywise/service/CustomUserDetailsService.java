package stephendowling.pennywise.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import stephendowling.pennywise.model.User;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.repository.UserRepository;

/*
 * retrieving a username, a password, and other attributes for authenticating with a username and password
 * when Spring Security authenticates a user, it uses CustomUserDetailsService to load the user
 * the returned CustomUserDetails is stored in the Authentication object
 * to access the user's ID, call getAuthenticatedUserId() and cast the principal to CustomUserDetails:
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //fetch user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        //convert user's role into GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = 
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        //build and return CustomUserDetails
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getEmail()
        );
    }
}
