package stephendowling.pennywise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//DTO for registering a new user - sent to backend  

public class RegistrationRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    //constructor getters and setters 
    public RegistrationRequest() {
    }

    public RegistrationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegistrationRequest username(String username) {
        setUsername(username);
        return this;
    }

    public RegistrationRequest email(String email) {
        setEmail(email);
        return this;
    }

    public RegistrationRequest password(String password) {
        setPassword(password);
        return this;
    }

    //debugging
    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
    
}
