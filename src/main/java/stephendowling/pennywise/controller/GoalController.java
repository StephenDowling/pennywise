package stephendowling.pennywise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stephendowling.pennywise.dto.GoalResponse;
import stephendowling.pennywise.model.Goal;
import stephendowling.pennywise.service.GoalService;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService){
        this.goalService=goalService;
    }

    /* ADMIN ONLY */
    //get all goals
    @GetMapping //http://localhost:8080/api/goals
    public List<GoalResponse> getAllGoals(){
        return goalService.findAll();
    }

    /* METHODS FOR ALL USERS */
    // Get all goals for the currently logged-in user
    @GetMapping("/my-goals") //http://localhost:8080/api/goals/my-goals
    public List<GoalResponse> getAllGoalsForAuthenticatedUser() {
        // Call the service method to get all categories for the authenticated user
        return goalService.getAllGoalsForCurrentUser();
    }

    // Create a new goal 
    @PostMapping //http://localhost:8080/api/goals
    public ResponseEntity<GoalResponse> createGoal(@RequestBody Goal goal) {
        GoalResponse response = goalService.create(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a goal 
    @PutMapping("/{id}") //http://localhost:8080/api/goals/{id}
    public ResponseEntity<GoalResponse> updateGoal(@RequestBody Goal goal, @PathVariable Integer id) {
        try {
            GoalResponse updatedGoal = goalService.update(goal, id);
            return ResponseEntity.ok(updatedGoal); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a goal
    @DeleteMapping("/{id}") //http://localhost:8080/api/goal/{id}
    public ResponseEntity<Void> deleteGoal(@PathVariable Integer id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
