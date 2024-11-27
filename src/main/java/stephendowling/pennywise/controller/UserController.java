package stephendowling.pennywise.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import stephendowling.pennywise.service.UserService;
import stephendowling.pennywise.dto.RegistrationRequest;
import stephendowling.pennywise.dto.RegistrationResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.User;

@RestController
@RequestMapping("/api/users") //http://localhost:8080/api/users end point 
public class UserController {

    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    
    /* ADMIN ONLY */

    //find all 
    @GetMapping("") //http://localhost:8080/api/users
    @PreAuthorize("hasAuthority('ADMIN')")
    List<UserSummary> findAll(){
        return userService.findAll();
    }

    //count 
    @GetMapping("/count") //http://localhost:8080/api/users/count
    public long count(){
        return userService.count();
    }

    //find by ID
    @GetMapping("/{id}") //http://localhost:8080/api/users/{id}
    UserSummary findById(@PathVariable Integer id){
        Optional<UserSummary> user = userService.findById(id); //using UserSummary DTO instead of User object 
        if(user.isEmpty()){ //if the User object is null
            throw new UserNotFoundException(); //throw this custom exception
        }
        return user.get(); //return the User object 
    }

    //find by username
    @GetMapping("/findByUsername/{username}") //http://localhost:8080/api/users/findByUsername/{username}
    public ResponseEntity<Optional<UserSummary>> getUserByUsername(@PathVariable String username) {
        Optional<UserSummary> user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    //delete a user by ID
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
        try {
            userService.deleteById(id);  // Delegate the delete action to the service
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // Return 204 No Content if deletion is successful
        } catch (UnauthorisedAccessException e) {
            // If the user is not an admin
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            // If the user with the given ID is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }


    /* METHODS FOR ALL USERS */
    //get current User
    @GetMapping("/me") //http://localhost:8080/api/users/me
    public ResponseEntity<UserSummary> getCurrentUser() {
        // Delegate the logic to the service
        UserSummary userSummary = userService.getCurrentUser();
        if (userSummary == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(userSummary);
    }

    //creating a new User
    @ResponseStatus(HttpStatus.CREATED) //sends a 201 back saying the User was created 
    @PostMapping("") //http://localhost:8080/api/users
    void create(@Valid @RequestBody User user){
        userService.create(user);
    }

    //update existing User
    @PutMapping("/{id}") //http://localhost:8080/api/users/{id}
    public ResponseEntity<UserSummary> update(@RequestBody User user, @PathVariable Integer id) {
        UserSummary updatedUser = userService.update(user, id);
        return ResponseEntity.ok(updatedUser); // Return 200 OK with the updated user
    }

    //delete a user
    @ResponseStatus(HttpStatus.NO_CONTENT) //sends back 204, done but no content to send back to you
    @DeleteMapping("/{id}") //http://localhost:8080/api/users/{id}
    void delete(@PathVariable Integer id){
        userService.delete(id);
    }

    //register new user
    @PostMapping("/register-new") //http://localhost:8080/api/users/register-new
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest request) {
        try {
            userService.registerUser(request); //service method
            //create a response DTO with a message
            RegistrationResponse response = new RegistrationResponse("User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            //return error in case of an exception
            RegistrationResponse errorResponse = new RegistrationResponse("Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


}
