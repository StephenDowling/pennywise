package stephendowling.pennywise.dto;

public class RegistrationResponse {
    
    private String message;

    // Constructor
    public RegistrationResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}
