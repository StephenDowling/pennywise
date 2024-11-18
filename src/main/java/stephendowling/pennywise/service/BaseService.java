package stephendowling.pennywise.service;

import org.springframework.security.core.context.SecurityContextHolder;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;

public abstract class BaseService {

    //common methods inherited by all services 

    //for user access controls 
    protected Integer getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        } else {
            throw new UnauthorisedAccessException("Unauthorized");
        }
    }

}
