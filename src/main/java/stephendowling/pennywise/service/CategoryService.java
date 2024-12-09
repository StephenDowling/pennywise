package stephendowling.pennywise.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import stephendowling.pennywise.config.CustomUserDetails;
import stephendowling.pennywise.dto.CategoryResponse;
import stephendowling.pennywise.dto.UserSummary;
import stephendowling.pennywise.exceptions.CategoryNotFoundException;
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

    /* ADMIN ONLY */
    // Find all categories
    public List<CategoryResponse> findAll() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
    
            //check if the user has ADMIN role
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    
            if (!isAdmin) {
                throw new UnauthorisedAccessException("Unauthorised access");
            }
        } else {
            throw new UnauthorisedAccessException("User not authenticated");
        }
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                      .map(this::mapToCategoryResponse) // map each Category to CategoryResponse
                      .collect(Collectors.toList());
    }

    /* METHODS FOR ALL USERS */
    // Get all categories for the currently logged-in user
    public List<CategoryResponse> getAllCategoriesForCurrentUser() {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        // fetch all categories for the authenticated user
        List<Category> categories = categoryRepository.findByUser_UserId(authenticatedUserId);

        // map the list of categories to CategoryResponses DTOs and return the list
        return categories.stream()
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toList());
    }

    //create a category
    public CategoryResponse create(Category category) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();
    
        //fetch the authenticated user from the database
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException());
    
        //check if a category with the same name already exists for the user
        boolean categoryExists = categoryRepository.existsByUserAndName(user, category.getName());
        if (categoryExists) {
            throw new IllegalArgumentException("A category with this name already exists.");
        }
    
        //associate the category with the authenticated user
        category.setUser(user);
    
        //save the Category entity
        category = categoryRepository.save(category);
    
        //map the saved Category entity to a CategoryResponse and return it
        return mapToCategoryResponse(category);
    }
    
    //update a category
    public CategoryResponse update(Category category, Integer id) {
        //get the authenticated user's ID
        Integer authenticatedUserId = getAuthenticatedUserId();

        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    //ensure the authenticated user is the one who owns the category
                    if (!existingCategory.getUser().getUserId().equals(authenticatedUserId)) {
                        throw new UnauthorisedAccessException("You can only update your own categories");
                    }

                    //update the category fields
                    existingCategory.setName(category.getName());

                    //save and return the updated category
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return mapToCategoryResponse(updatedCategory); 
                })
                .orElseThrow(() -> new CategoryNotFoundException());
    }

    //delete a category
    public void delete(Integer categoryId) {
        Integer authenticatedUserId = getAuthenticatedUserId();

        //rtrieve the category by its ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());

        //verify that the category belongs to the authenticated user
        if (!category.getUser().getUserId().equals(authenticatedUserId)) {
            throw new UnauthorisedAccessException("Unauthorised delete attempt");
        }

        //delete the category
        categoryRepository.deleteById(categoryId);
    }

    //helper method to map Category to CategoryResponse DTO
    private CategoryResponse mapToCategoryResponse(Category category) {
        User user = category.getUser(); //get associated user from Category
        
        //map User to UserSummary DTO
        UserSummary userSummary = new UserSummary(user.getUserId(), user.getUsername());
        
        //create and return the categoryresponse DTO
        return new CategoryResponse(
                category.getName(),
                category.getCategoryId(),
                userSummary 
        );
    }
    
}
