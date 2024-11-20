package stephendowling.pennywise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import stephendowling.pennywise.dto.BudgetResponse;
import stephendowling.pennywise.model.Budget;
import stephendowling.pennywise.service.BudgetService;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /* ADMIN ONLY */
    // Get all budgets 
    @GetMapping //http://localhost:8080/api/budgets
    public List<BudgetResponse> getAllBudgets() {
        return budgetService.findAll();  // Service now returns List<BudgetResponse>
    }

    /* METHODS FOR ALL USERS */
    // Get all budgets for the currently logged-in user
    @GetMapping("/my-budgets") //http://localhost:8080/api/budgets/my-budgets
    public List<BudgetResponse> getAllBudgetsForAuthenticatedUser() {
        // Call the service method to get all budgets for the authenticated user
        return budgetService.getAllBudgetsForCurrentUser();
    }

    // Create a new budget 
    @PostMapping //http://localhost:8080/api/budgets
    public ResponseEntity<BudgetResponse> createBudget(@RequestBody Budget budget) {
        BudgetResponse response = budgetService.create(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a budget 
    @PutMapping("/{id}") //http://localhost:8080/api/budgets/{id}
    public ResponseEntity<BudgetResponse> updateBudget(@RequestBody Budget budget, @PathVariable Integer id) {
        try {
            BudgetResponse updatedBudget = budgetService.update(budget, id);
            return ResponseEntity.ok(updatedBudget);  // Returning BudgetResponse
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a budget
    @DeleteMapping("/{id}") //http://localhost:8080/api/budgets/{id}
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
