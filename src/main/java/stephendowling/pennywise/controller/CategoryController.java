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

import stephendowling.pennywise.dto.CategoryResponse;
import stephendowling.pennywise.model.Category;
import stephendowling.pennywise.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService=categoryService;
    }

    /* ADMIN ONLY */
    // Find all categories
    @GetMapping //http://localhost:8080/api/categories
    public List<CategoryResponse> getAllCategories() {
        return categoryService.findAll();  
    }

    /* METHODS FOR ALL USERS */
    // Get all categories for the currently logged-in user
    @GetMapping("/my-categories") //http://localhost:8080/api/categories/my-categories
    public List<CategoryResponse> getAllCategoriesForAuthenticatedUser() {
        // Call the service method to get all categories for the authenticated user
        return categoryService.getAllCategoriesForCurrentUser();
    }
    
    // Create a new category 
    @PostMapping //http://localhost:8080/api/categories
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Category category) {
        CategoryResponse response = categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a category 
    @PutMapping("/{id}") //http://localhost:8080/api/categories/{id}
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody Category category, @PathVariable Integer id) {
        try {
            CategoryResponse updatedCategory = categoryService.update(category, id);
            return ResponseEntity.ok(updatedCategory); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a category
    @DeleteMapping("/{id}") //http://localhost:8080/api/categories/{id}
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
