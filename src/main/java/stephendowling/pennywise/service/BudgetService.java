package stephendowling.pennywise.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.BudgetResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.Budget;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.BudgetRepository;
import stephendowling.pennywise.repository.UserRepository;

import java.util.List;
//import java.util.Optional;
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

    // Find all budgets, map User to UserSummary - Admin only method 
    public List<BudgetResponse> findAll() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
    
            // Check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    
            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorized access");
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }

        List<Budget> budgets = budgetRepository.findAll();
        return budgets.stream()
                      .map(this::mapToBudgetResponse) // map each Budget to BudgetResponse
                      .collect(Collectors.toList());
    }

    // Find budget by ID, map User to UserSummary
    public BudgetResponse findById(Integer id) {

        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id) ) {
            throw new UnauthorisedAccessException("Unauthorized access to another user's data");
        }

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        return mapToBudgetResponse(budget);
    }

    // Create a new budget
    public BudgetResponse create(Budget budget) {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());

        // Associate the budget with the authenticated user
        budget.setUser(user);

        // Save the Budget entity
        budget = budgetRepository.save(budget);

        // Map the saved Budget entity to a BudgetResponse and return it
        return mapToBudgetResponse(budget);
    }

    // Update an existing budget
    public BudgetResponse update(Budget budget, Integer id) {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return budgetRepository.findById(id)
                .map(existingBudget -> {
                    // Ensure the authenticated user is the one who owns the budget
                    if (!existingBudget.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own budgets");
                    }

                    // Update the budget fields
                    existingBudget.setAmount(budget.getAmount());
                    existingBudget.setStartDate(budget.getStartDate());
                    existingBudget.setEndDate(budget.getEndDate());

                    // Ensure user is still associated with the authenticated user (for consistency)
                    existingBudget.setUser(existingBudget.getUser());  // No need to update the user, it's already the authenticated user

                    // Save and return the updated budget
                    Budget updatedBudget = budgetRepository.save(existingBudget);
                    return mapToBudgetResponse(updatedBudget); // Return mapped response instead of Budget
                })
                .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    // Delete a budget
    public void delete(Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized delete attempt");
        }
        budgetRepository.deleteById(id);
    }

    // Get all budgets for the currently logged-in user
    public List<BudgetResponse> getAllBudgetsForAuthenticatedUser() {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch all budgets for the authenticated user
        List<Budget> budgets = budgetRepository.findByUser_UserId(authenticatedUserId);

        // Map the list of budgets to BudgetResponse DTOs and return the list
        return budgets.stream()
                    .map(this::mapToBudgetResponse)
                    .collect(Collectors.toList());
    }


    // Helper method to map Budget to BudgetResponse DTO
    private BudgetResponse mapToBudgetResponse(Budget budget) {
        User user = budget.getUser(); // Get associated user from Budget
        
        // Map User to UserSummary DTO (avoiding full User details like password)
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        // Create and return the BudgetResponse DTO, including the UserSummary
        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getAmount(),
                budget.getStartDate(),
                budget.getEndDate(),
                userSummary // Only user ID and username will be included, no password
        );
    }
}
