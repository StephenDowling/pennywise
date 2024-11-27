package stephendowling.pennywise.dto;

// DTO for returning a response for a category request  
// should display category info then a UserSummary DTO

public class CategoryResponse {
    
    private String name;
    private Integer categoryId;
    private UserSummary user;

    //constructor getters and setters 
    public CategoryResponse() {
    }

    public CategoryResponse(String name, Integer categoryId, UserSummary user) {
        this.name = name;
        this.categoryId = categoryId;
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

    
}
