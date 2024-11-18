package stephendowling.pennywise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.CategoryResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.UnauthorisedAccessException;
import stephendowling.pennywise.exceptions.UserNotFoundException;
import stephendowling.pennywise.model.Category;
import stephendowling.pennywise.model.User;
import stephendowling.pennywise.repository.CategoryRepository;
import stephendowling.pennywise.repository.UserRepository;

@Service
public class CategoryService extends BaseService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository){
        this.categoryRepository=categoryRepository;
        this.userRepository=userRepository;
    }

    public List<CategoryResponse> findAll() {
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
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                      .map(this::mapToCategoryResponse) // map each Category to BudgetResponse
                      .collect(Collectors.toList());
    }

    //create a new category
    public CategoryResponse create(Category category) {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                                .orElseThrow(() -> new UserNotFoundException());

        // Associate the budget with the authenticated user
        category.setUser(user);

        // Save the Budget entity
        category = categoryRepository.save(category);

        // Map the saved Budget entity to a BudgetResponse and return it
        return mapToCategoryResponse(category);
    }

    //update Category
    public CategoryResponse update(Category category, Integer id) {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Ensure the authenticated user is the one who owns the category
                    if (!existingCategory.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own categories");
                    }

                    // Update the category fields
                    existingCategory.setName(category.getName());

                    // Ensure user is still associated with the authenticated user (for consistency)
                    existingCategory.setUser(existingCategory.getUser());  // No need to update the user, it's already the authenticated user

                    // Save and return the updated budget
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return mapToCategoryResponse(updatedCategory); 
                })
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void delete(Integer id) {
        Integer authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(id)) {
            throw new UnauthorisedAccessException("Unauthorized delete attempt");
        }
        categoryRepository.deleteById(id);
    }

    public List<CategoryResponse> getAllCategoriesForAuthenticatedUser() {
        // Get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // Fetch all budgets for the authenticated user
        List<Category> categories = categoryRepository.findByUser_UserId(authenticatedUserId);

        // Map the list of budgets to BudgetResponse DTOs and return the list
        return categories.stream()
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toList());
    }

    // Helper method to map Category to CategoryResponse DTO
    private CategoryResponse mapToCategoryResponse(Category category) {
        User user = category.getUser(); // Get associated user from Category
        
        // Map User to UserSummary DTO (avoiding full User details like password)
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        // Create and return the BudgetResponse DTO, including the UserSummary
        return new CategoryResponse(
                category.getName(),
                userSummary // Only user ID and username will be included, no password
        );
    }
    
}
