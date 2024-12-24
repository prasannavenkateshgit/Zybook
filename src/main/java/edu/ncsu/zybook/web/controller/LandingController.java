package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.persistence.repository.UserRepository;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.impl.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/landing")
public class LandingController {

    private UserRepository userRepository;
    private CourseService courseService;

    public LandingController(UserRepository userRepository, CourseService courseService) {
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @GetMapping()
    public String landing(@AuthenticationPrincipal CustomUserDetails currentUser) {
        String role = userRepository.findById(currentUser.getId()).get().getRoleName();
        if(role.equals("")) return "redirect:/landing/student";
        if(role.equals("student")) return "redirect:/landing/studentnew";

        return "redirect:/landing/"+ role;
    }

    @GetMapping("/admin")
    public String adminLanding() {
        return "landing/adminlanding";
    }

    @GetMapping("/faculty")
    public String facultyLanding(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("userId",currentUser.getId());
        return "landing/facultylanding";
    }

    @GetMapping("/student")
    public String studentLanding( ) {
        return "landing/studentlanding";
    }

    @GetMapping("/studentnew")
    public String studentnewLanding(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("userId", currentUser.getId());
        return "landing/studentnewlanding";
    }

    @GetMapping("/ta")
    public String taLanding(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("userId",currentUser.getId());
        return "landing/talanding";
    }

    @GetMapping("/active")
    public String activeLanding(@RequestParam("userId") int userId, @RequestParam("courseId") String courseId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("courseId", courseId);
        int tbookId = courseService.getTbookId(courseId);
        model.addAttribute("tbookId", tbookId);
        System.out.println("Textbook Id"+tbookId);
        return "landing/activelanding";
    }

    @GetMapping("/queries")
    public String queries(){
        return "landing/queries";
    }

}