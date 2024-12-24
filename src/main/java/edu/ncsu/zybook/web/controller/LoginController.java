package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.domain.model.User;
import edu.ncsu.zybook.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final IUserService userService;

    @Autowired
    public LoginController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String loginPage(@RequestParam(name = "role", required = false) String roleName, Model model, @ModelAttribute User user) {
        model.addAttribute("role", roleName != null ? roleName : "Unknown");
        System.out.println("Login Page controller:"+roleName);
        return "login/login";
    }

    @PostMapping
    public String authenticateUser(Model model, @ModelAttribute User checkUser) {
        System.out.println("Check User object"+checkUser);
        Optional<User> userOptional = userService.findByEmail(checkUser.getEmail());
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(checkUser.getPassword())) {
            User user = userOptional.get();
            String role = user.getRoleName();
            System.out.println("Inside authenticated user:"+user);
            if (role == null) {
                model.addAttribute("error", "User role is not assigned.");
                return "login/login";
            }

            switch (role.toLowerCase()) {
                case "student":
                    return "redirect:/landing/studentnew";
                case "faculty":
                    return "redirect:/landing/faculty";
                case "admin":
                    return "redirect:/landing/admin";
                case "ta":
                    return "redirect:/landing/ta";
                default:
                    model.addAttribute("error", "Invalid role assigned to user.");
                    return "login/login";
            }
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login/login";
        }
    }
}
