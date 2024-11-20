package stephendowling.pennywise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.GoalResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.CategoryNotFoundException;
import stephendowling.pennywise.exceptions.GoalNotFoundException;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.Goal;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.GoalRepository;
import stephendowling.pennywise.repository.UserRepository;

@Service
public class GoalService extends BaseService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Autowired
    public GoalService(UserRepository userRepository, GoalRepository goalRepository){
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    /* ADMIN ONLY */
    //Find all Goals
    public List<GoalResponse> findAll() {
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
        List<Goal> goals = goalRepository.findAll();
        return goals.stream()
                      .map(this::mapToGoalResponse) // map each Category to BudgetResponse
                      .collect(Collectors.toList());
    }

    /* METHODS FOR ALL USERS */
    //get all goals for current user
    public List<GoalResponse> getAllGoalsForCurrentUser() {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch all budgets for the authenticated user
        List<Goal> goals = goalRepository.findByUser_UserId(authenticatedUserId);

        // Map the list of budgets to BudgetResponse DTOs and return the list
        return goals.stream()
                    .map(this::mapToGoalResponse)
                    .collect(Collectors.toList());
    }

    //create a new goal 
    public GoalResponse create(Goal goal) {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());

        // Associate the budget with the authenticated user
        goal.setUser(user);

        // Save the Budget entity
        goal = goalRepository.save(goal);

        // Map the saved Budget entity to a BudgetResponse and return it
        return mapToGoalResponse(goal);
    }

    //update a goal
    public GoalResponse update(Goal goal, Integer id) {
       // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return goalRepository.findById(id)
                .map(existingGoal -> {
                    // Ensure the authenticated user is the one who owns the category
                    if (!existingGoal.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own categories");
                    }

                    // Update the goal fields
                    existingGoal.setName(goal.getName());

                    // Ensure user is still associated with the authenticated user (for consistency)
                    existingGoal.setUser(existingGoal.getUser());  // No need to update the user, it's already the authenticated user

                    // Save and return the updated budget
                    Goal updatedGoal = goalRepository.save(existingGoal);
                    return mapToGoalResponse(updatedGoal); 
                })
                .orElseThrow(() -> new GoalNotFoundException());
    }

    //delete a goal
    public void delete(Integer goalId) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Retrieve the category by its ID
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CategoryNotFoundException());

        // Verify that the category belongs to the authenticated user
        if (!goal.getUser().getUserId().equals(authenticatedUserId)) {
            throw new UnauthorisedAccessException("Unauthorised delete attempt");
        }

        // If everything is valid, delete the category
        goalRepository.deleteById(goalId);
    }

    //helper method for mapping Goal to GoalResponse DTO
    private GoalResponse mapToGoalResponse(Goal goal){
       User user = goal.getUser(); // Get associated user from Goal
        
        // Map User to UserSummary DTO (avoiding full User details like password)
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        // Create and return the BudgetResponse DTO, including the UserSummary
        return new GoalResponse(
                goal.getGoalId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getDeadline(),
                userSummary // Only user ID and username will be included, no password
        );
    }
    
}
