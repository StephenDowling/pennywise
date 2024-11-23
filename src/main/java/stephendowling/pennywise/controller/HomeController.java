package stephendowling.pennywise.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    // @GetMapping("/") // Maps the root URL
    // public String homePage() {
    //     return "index"; // Return the name of the HTML file (index.html) located in `src/main/resources/templates`
    // }

    @GetMapping("/login") // Maps the root URL
    public String logIn() {
        return "login"; // Return the name of the HTML file (login.html) located in `src/main/resources/templates`
    }

    @GetMapping("/register") // Maps the "/register" URL
    public String register() {
        return "register"; // Return the name of the HTML file (register.html)
    }

    @GetMapping("/password") // Maps the "/password" URL
    public String password() {
        return "password"; // Return the name of the HTML file (register.html)
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


    //retrieves username for use in index.html
    @GetMapping("/")
    public String homePage(Model model) {
        // Get the logged-in user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username); // Pass it to the view
        return "index"; // The name of your template (e.g., index.html)
    }
}
