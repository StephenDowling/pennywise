package stephendowling.pennywise.exceptions;

//customer error message 
public class UnauthorisedAccessException extends RuntimeException  {
    public UnauthorisedAccessException(String message) {
        super(message);
    }
}
