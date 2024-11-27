package stephendowling.pennywise.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class HomeController { //for routing requests to HTML pages 

    //so username is visibile in every page 
   @ModelAttribute
    public void addUserToModel(Model model) {
        //get the logged-in user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); //pass it to the view
            model.addAttribute("username", username);
        }
    }

    //log in page with error handling 
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        return "login"; //return the login HTML page
    }


    @GetMapping("/register") //maps the "/register" URL
    public String register() {
        return "register"; //return the name of the HTML file (register.html)
    }

    @GetMapping("/password") //maps the "/password" URL
    public String password() {
        return "password"; //return the name of the HTML file (password.html)
    }

    @GetMapping("/update")
    public String update(){
        return "update";
    }

    @GetMapping("/delete")
    public String delete(){
        return "delete";
    }

    @GetMapping("/charts")
    public String charts(){
        return "charts";
    }

    @GetMapping("/tables")
    public String tables(){
        return "tables";
    }

    @GetMapping("/transactions")
    public String transactions(){
        return "transactions";
    }

    @GetMapping("/categories")
    public String categories(){
        return "categories";
    }

    @GetMapping("/goals")
    public String goals(){
        return "goals";
    }

    @GetMapping("/budgets")
    public String budgets(){
        return "budgets";
    }

    @GetMapping("/currency")
    public String currency(){
        return "currency";
    }

    //retrieves home page 
    @GetMapping("/")
    public String homePage() {
        return "index"; 
    }
}
