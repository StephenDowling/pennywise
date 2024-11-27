package stephendowling.pennywise.dto;

//DTO for responding to registration requests 
public class RegistrationResponse {
    
    private String message;

    //constructor
    public RegistrationResponse(String message) {
        this.message = message;
    }

    //getter
    public String getMessage() {
        return message;
    }

    //setter
    public void setMessage(String message) {
        this.message = message;
    }
}
