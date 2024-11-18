package stephendowling.pennywise.exceptions;

public class UnauthorisedAccessException extends RuntimeException  {
    public UnauthorisedAccessException(String message) {
        super(message);
    }
}
