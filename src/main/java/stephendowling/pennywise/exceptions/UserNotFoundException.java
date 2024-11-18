package stephendowling.pennywise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException { //customer exception for not being able to find a User
    public UserNotFoundException(){
        super("User not found!");
    }
}
