package stephendowling.pennywise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.RegistrationRequest;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.UserRepository;

@Service
public class UserService extends BaseService {
    
    private final UserRepository userRepository; //for talking to DB
    private final PasswordEncoder passwordEncoder; //for hashing passwords 

    @Autowired //contructor with pw encoder and UserRepo for talking to DB
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    
    // Find all users - Admin only method 
    public List<UserSummary> findAll() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
    
            // Check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    
            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorized access");
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
        // Map all Users to UserSummary
        return userRepository.findAll()
                            .stream()
                            .map(this::mapToUserSummary) 
                            .toList();
    }

    // Count the total number of users - Admin only
    public long count(){
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
        
                // Check if the user has ADMIN role
                boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
        
                if (!isAdmin) {
                    throw new UnauthorisedAccessException("Unauthorized access");
                }
            } else {
                throw new RuntimeException("User not authenticated");
            }
            return userRepository.count();
        }

    // Find user by ID 
    public Optional<UserSummary> findById(Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id) ) {
            throw new UnauthorisedAccessException("Unauthorized access to another user's data");
        }
        return userRepository.findById(id)
                            .map(this::mapToUserSummary);
    }
    

    // Create a new User (with encoded password) - different to a user registering themselves 
    public User create(User user) {
        if (user.getPassword() != null) { //error handling making sure user has provided password 
            // Encode the password before saving using the password encoder we imported 
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword); //set the user's password to this encoded password 
        }
        return userRepository.save(user); //save this to the DB
    }

    // Update an existing user (with encoded password if updated)
    public UserSummary update(User user, Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized update attempt");
        }
    
        return userRepository.findById(id)
            .map(existingUser -> {
                if (user.getUsername() != null) {
                    existingUser.setUsername(user.getUsername());
                }
                if (user.getEmail() != null) {
                    existingUser.setEmail(user.getEmail());
                }
                if (user.getPassword() != null) {
                    String encodedPassword = passwordEncoder.encode(user.getPassword());
                    existingUser.setPassword(encodedPassword);
                }
                User updatedUser = userRepository.save(existingUser);
                return mapToUserSummary(updatedUser);
            })
            .orElseThrow(() -> new UserNotFoundException());
    }
    

    // Delete a user by ID
    public void delete(Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized delete attempt");
        }
        userRepository.deleteById(id);
    }
    
    // Find a user by username
    public Optional<User> findByUsername(String username) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authenticatedUsername.equals(username)) {
            throw new UnauthorisedAccessException("Unauthorized access to another user's data");
        }
        return userRepository.findByUsername(username);
    }
    

    //register a new user 
    public User registerUser(RegistrationRequest request) { //request is a DTO from the user 
        // Check for existing username or email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Map DTO to entity
        User user = new User();
        user.setUsername(request.getUsername()); 
        user.setEmail(request.getEmail());

        // Encode the password
        if (request.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }

        // Set default role
        user.setRole("USER");

        // Save and return the user 
        return userRepository.save(user);

    }

    //helper method to map User objects to UserSummary DTOs 
    private UserSummary mapToUserSummary(User user) {
        return new UserSummary(user.getUserId(), user.getUsername());
    }
}
