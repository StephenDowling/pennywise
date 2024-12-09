package stephendowling.pennywise.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.BudgetResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.BudgetNotFoundException;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.Budget;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.BudgetRepository;
import stephendowling.pennywise.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService extends BaseService{

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    /* ADMIN ONLY */
    // Find all budgets
    public List<BudgetResponse> findAll() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
    
            //check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    
            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorized access");
            }
        } else {
            throw new UnauthorisedAccessException("User not authenticated");
        }

        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream()
                      .map(this::mapToBudgetResponse) // map each Budget to BudgetResponse
                      .collect(Collectors.toList());
    }

    /* METHODS FOR ALL USERS */
    //get all budgets for the currently logged-in user
    public List<BudgetResponse> getAllBudgetsForCurrentUser() {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        //fetch all budgets for the authenticated user
        List<Budget> budgets = budgetRepository.findByUser_UserId(authenticatedUserId);

        //map the list of budgets to BudgetResponse DTOs and return the list
        return budgets.stream()
                    .map(this::mapToBudgetResponse)
                    .collect(Collectors.toList());
    }
        
    //create a new budget
    public BudgetResponse create(Budget budget) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        //fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());

        //associate the budget with the authenticated user
        budget.setUser(user);

        //save the Budget entity
        budget = budgetRepository.save(budget);

        //map the saved Budget entity to a BudgetResponse and return it
        return mapToBudgetResponse(budget);
    }

    //update an existing budget
    public BudgetResponse update(Budget budget, Integer id) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return budgetRepository.findById(id)
                .map(existingBudget -> {
                    //ensure the authenticated user is the one who owns the budget
                    if (!existingBudget.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own budgets");
                    }

                    //update the budget fields
                    existingBudget.setAmount(budget.getAmount());
                    existingBudget.setStartDate(budget.getStartDate());
                    existingBudget.setEndDate(budget.getEndDate());
                    existingBudget.setName(budget.getName());

                    //save and return the updated budget
                    Budget updatedBudget = budgetRepository.save(existingBudget);
                    return mapToBudgetResponse(updatedBudget); //return mapped response instead of Budget
                })
                .orElseThrow(() -> new BudgetNotFoundException());
    }

    //delete a budget
    public void delete(Integer budgetId) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        //retrieve the budget by its ID
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException());

        //verify that the budget belongs to the authenticated user
        if (!budget.getUser().getUserId().equals(authenticatedUserId)) {
            throw new UnauthorisedAccessException("Unauthorised delete attempt");
        }

        //if everything is valid, delete the budget
        budgetRepository.deleteById(budgetId);
    }

    //helper method to map Budget to BudgetResponse DTO
    private BudgetResponse mapToBudgetResponse(Budget budget) {
        User user = budget.getUser(); //get associated user from Budget
        
        //map User to UserSummary DTO - used to avoid sending password in response 
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        //create and return the BudgetResponse DTO
        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getAmount(),
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getName(),
                userSummary 
                
        );
    }
}
