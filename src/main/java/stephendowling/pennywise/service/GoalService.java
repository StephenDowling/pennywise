package stephendowling.pennywise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.GoalResponse;
import stephendowling.pennywise.dto.UserSummary;
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
    
            //check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    
            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorized access");
            }
        } else {
            throw new UnauthorisedAccessException("User not authenticated");
        }
        List<Goal> goals = goalRepository.findAll();
        return goals.stream()
                      .map(this::mapToGoalResponse) // map each goal to GoalResponse
                      .collect(Collectors.toList());
    }

    /* METHODS FOR ALL USERS */
    //get all goals for current user
    public List<GoalResponse> getAllGoalsForCurrentUser() {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        //fetch all goals for the authenticated user
        List<Goal> goals = goalRepository.findByUser_UserId(authenticatedUserId);

        //map the list of goals to goalResponse DTOs and return the list
        return goals.stream()
                    .map(this::mapToGoalResponse)
                    .collect(Collectors.toList());
    }

    //create a new goal 
    public GoalResponse create(Goal goal) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        //fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());

        //associate the goal with the authenticated user
        goal.setUser(user);

        //save the goal entity
        goal = goalRepository.save(goal);

        //map the saved goal entity to a GoalResponse and return it
        return mapToGoalResponse(goal);
    }

    //update a goal
    public GoalResponse update(Goal goal, Integer id) {
       //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return goalRepository.findById(id)
                .map(existingGoal -> {
                    //ensure the authenticated user is the one who owns the goal
                    if (!existingGoal.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own goals");
                    }

                    //update the goal fields
                    existingGoal.setName(goal.getName());
                    existingGoal.setTargetAmount(goal.getTargetAmount());
                    existingGoal.setCurrentAmount(goal.getCurrentAmount());
                    existingGoal.setDeadline(goal.getDeadline());
                    existingGoal.setStatus(goal.getStatus());

                    //save and return the updated goal
                    Goal updatedGoal = goalRepository.save(existingGoal);
                    return mapToGoalResponse(updatedGoal); 
                })
                .orElseThrow(() -> new GoalNotFoundException());
    }

    //delete a goal
    public void delete(Integer goalId) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        //retrieve the goal by its ID
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException());

        //verify that the goal belongs to the authenticated user
        if (!goal.getUser().getUserId().equals(authenticatedUserId)) {
            throw new UnauthorisedAccessException("Unauthorised delete attempt");
        }

        //delete the goal
        goalRepository.deleteById(goalId);
    }

    //helper method for mapping Goal to GoalResponse DTO
    private GoalResponse mapToGoalResponse(Goal goal){
       User user = goal.getUser(); //get associated user from Goal
        
        //map User to UserSummary DTO
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        //create and return the GoalResponse DTO
        return new GoalResponse(
                goal.getGoalId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getStatus(),
                userSummary 
        );
    }
    
}
