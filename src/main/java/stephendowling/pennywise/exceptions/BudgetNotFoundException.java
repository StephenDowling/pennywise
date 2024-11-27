package stephendowling.pennywise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//customer error message 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(){
        super("Budget not found!");
    }
}
