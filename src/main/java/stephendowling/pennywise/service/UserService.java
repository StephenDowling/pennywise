package stephendowling.pennywise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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

    /* ADMIN ONLY */
    // Find all users
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

    // Count the total number of users
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
        return userRepository.findById(id)
                            .map(this::mapToUserSummary);
    }

    // Find a user by username
    public Optional<UserSummary> findByUsername(String username) {
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
        // Fetch the user and map to UserSummary if found
        return userRepository.findByUsername(username)
            .map(this::mapToUserSummary); // Use the helper method to map
    }

    // Delete user by ID
    public void deleteById(Integer id) {
        // Get the current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Check if the authenticated user is an instance of CustomUserDetails
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            
            // Check if the user has the "ADMIN" role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            
            if (!isAdmin) {
                // If not an admin, throw an UnauthorizedAccessException
                throw new UnauthorisedAccessException("Unauthorized access");
            }
        } else {
            // If the user is not authenticated, throw a runtime exception
            throw new RuntimeException("User not authenticated");
        }
        
        // Proceed with deleting the user if they exist
        Optional<User> userToDelete = userRepository.findById(id);
        
        if (userToDelete.isPresent()) {
            userRepository.delete(userToDelete.get());
        } else {
            // Throw an exception if no user is found with the given ID
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }


    
    /* METHODS FOR ALL USERS */
    //get current user 
    public UserSummary getCurrentUser() {
        Integer authenticatedUserId = getAuthenticatedUserId(); // Reuse existing method for user ID
    
        // Retrieve the authenticated user from the database
        return userRepository.findById(authenticatedUserId)
            .map(this::mapToUserSummary) // Reuse mapping helper method
            .orElseThrow(() -> new UserNotFoundException());
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
        Integer authenticatedUserId = getAuthenticatedUserId(); // Reuse existing method for user ID
        
        // Check if the user is allowed to update their own details (authorization check)
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized update attempt");
        }
    
        // Retrieve the current user from the database to check the current username/email
        Optional<User> currentUserOptional = userRepository.findById(id);
        if (!currentUserOptional.isPresent()) {
            throw new UserNotFoundException();
        }
    
        User currentUser = currentUserOptional.get();
    
        // Check if the username is being changed
        if (user.getUsername() != null && !user.getUsername().equals(currentUser.getUsername())) {
            // If the username is different, check if the new username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("Username already exists.");
            }
        }
    
        // Check if the email is being changed
        if (user.getEmail() != null && !user.getEmail().equals(currentUser.getEmail())) {
            // If the email is different, check if the new email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }
        }
    
        // Now proceed to update the user fields
        if (user.getUsername() != null) {
            currentUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            currentUser.setPassword(encodedPassword);
        }
    
        // Save the updated user and return the UserSummary DTO
        User updatedUser = userRepository.save(currentUser);  // Save the updated user
        return mapToUserSummary(updatedUser);
    }
    

    // Delete a user by ID
    public void delete(Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized delete attempt");
        }
        userRepository.deleteById(id);
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
