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

import jakarta.validation.Valid;
import stephendowling.pennywise.service.UserService;
import stephendowling.pennywise.dto.RegistrationRequest;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.User;

@RestController
@RequestMapping("/api/users") //http://localhost:8080/api/users
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

    /* METHODS FOR ALL USERS */
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
    @PostMapping("/register") //http://localhost:8080/api/users/register
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }


}
