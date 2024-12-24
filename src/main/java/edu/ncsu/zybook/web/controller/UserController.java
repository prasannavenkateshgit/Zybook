package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.domain.model.Notification;
import edu.ncsu.zybook.domain.model.User;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.security.SecurityConfig;
import edu.ncsu.zybook.service.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;
    SecurityConfig securityConfig;

    public UserController(IUserService userService, SecurityConfig securityConfig) {
        this.userService = userService;
        this.securityConfig = securityConfig;
    }

    // Allow user to view their own profile or allow access to admins
    @PreAuthorize("#id == principal.id or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public String getUser(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable int id, Model model) {
        System.out.println("Current logged in User "+ currentUser.getId());
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/user";
        } else {
            return "user/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.create(user);
        return "redirect:/users";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    @GetMapping("/newta")
    public String showCreateTAForm(@RequestParam("courseId") String courseId, Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("courseId", courseId);
        return "user/createta";
    }

    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    @PostMapping("/ta")
    public String createTA(@AuthenticationPrincipal CustomUserDetails currentUser, @ModelAttribute User user, @RequestParam("courseId") String courseId) {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createTA(user, courseId);
        String currentUserRole = userService.getUserRole(currentUser.getId());
        if(currentUserRole.equalsIgnoreCase("FACULTY")) {
            return "redirect:/landing/faculty";
        }
        return "redirect:/users";
    }

    // Allow user to edit their own profile or allow access to admins
    @PreAuthorize("#id == principal.id or hasAnyRole('ADMIN','FACULTY','TA')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            user.get().setPassword("");
            model.addAttribute("user", user.get());
            return "user/create";
        } else {
            return "user/not-found";
        }
    }

    // Allow user to update their own profile or allow access to admins
    @PreAuthorize("#userId == principal.id or hasRole('ADMIN') or hasRole('FACULTY')")
    @PutMapping()
    public String updateUser(@ModelAttribute User user, @RequestParam("userId") int userId) {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        System.out.println(user);
        if(user.getNewPassword().equals(user.getConfirmPassword())) {
            String currentPasswordEntered = user.getPassword();
            String passwordFromDb = userService.getPassword(user);

            if (passwordEncoder.matches(currentPasswordEntered, passwordFromDb)){
                System.out.println("Entered Password equals db password");
                user.setNewPassword(passwordEncoder.encode(user.getNewPassword()));
                Optional<User> updatedUser = userService.update(user);

            }
        }
        return "redirect:/users/"+userId;
    }

    // Only admins can delete a user
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.delete(id);
        return "redirect:/users";
    }

    @GetMapping("/notifications/{id}")
    public String getNotifications(@PathVariable int id, Model model) {
        List<Notification> notifications = userService.getNotification(id);
        model.addAttribute("notifications", notifications);
        return "user/notifications";
    }

    @PreAuthorize("hasRole('FACULTY')")
    @GetMapping("/waiting/{id}")
    public String getWaitingList(@PathVariable String id, Model model) {
        List<User> users = userService.getWaitingList(id);
        model.addAttribute("users", users);
        model.addAttribute("courseId",id);
        return "user/waitlist";
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping("/approve/{courseId}/{userId}")
    public String approve(@PathVariable String courseId, @PathVariable int userId, Model model) {
        userService.approve(courseId, userId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("userId", userId);
        return "redirect:/users/waiting/" + courseId;
    }

    @PreAuthorize("hasRole('FACULTY')")
    @PostMapping("/reject/{courseId}/{userId}")
    public String reject(@PathVariable String courseId, @PathVariable int userId, Model model) {
        userService.reject(courseId, userId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("userId", userId);
        return "redirect:/users/waiting/" + courseId;
    }
}
