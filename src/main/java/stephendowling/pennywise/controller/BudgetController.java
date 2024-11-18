package stephendowling.pennywise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import stephendowling.pennywise.dto.BudgetResponse;
//import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.model.Budget;
import stephendowling.pennywise.service.BudgetService;

import java.util.List;
//import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Get all budgets (returning a List of BudgetResponse DTOs instead of List<Budget>)
    @GetMapping //http://localhost:8080/api/budgets
    public List<BudgetResponse> getAllBudgets() {
        return budgetService.findAll();  // Service now returns List<BudgetResponse>
    }

    // Get budget by ID (returning BudgetResponse instead of Budget)
    @GetMapping("/{id}") //http://localhost:8080/api/budgets/{id}
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Integer id) {
    try {
        BudgetResponse budgetResponse = budgetService.findById(id); // Get the BudgetResponse from the service
        return ResponseEntity.ok(budgetResponse); // Return the BudgetResponse wrapped in ResponseEntity
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build(); // Return 404 if Budget not found
    }
}




    // Create a new budget (this already returns BudgetResponse, no change needed here)
    @PostMapping //http://localhost:8080/api/budgets
    public ResponseEntity<BudgetResponse> createBudget(@RequestBody Budget budget) {
        BudgetResponse response = budgetService.create(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a budget (updating Budget, return BudgetResponse to avoid exposing sensitive data)
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

    // Endpoint to fetch all budgets for the authenticated user
    @GetMapping("/my-budgets") //http://localhost:8080/api/budgets/my-budgets
    public List<BudgetResponse> getAllBudgetsForAuthenticatedUser() {
        // Call the service method to get all budgets for the authenticated user
        return budgetService.getAllBudgetsForAuthenticatedUser();
    }
}
