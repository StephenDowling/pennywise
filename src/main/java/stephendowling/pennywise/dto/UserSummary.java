package stephendowling.pennywise.dto;

//DTO used as a response when making changes involving a specific user 
//should return the user's ID and username, but not their password as was previously 

public class UserSummary {
    private Integer userId;
    private String username;

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Constructor, getters, setters
    public UserSummary(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
