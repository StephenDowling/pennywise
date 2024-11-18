package stephendowling.pennywise.dto;

// DTO for returning when making changes to a category 
// should display category info then a UserSummary DTO

public class CategoryResponse {
    
    private String name;
    private UserSummary user;


    public CategoryResponse() {
    }

    public CategoryResponse(String name, UserSummary user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

    
}
