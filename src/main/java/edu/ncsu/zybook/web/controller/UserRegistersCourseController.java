package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.UserWeakDTO;
import edu.ncsu.zybook.domain.model.*;
import edu.ncsu.zybook.mapper.UserWeakDTOMapper;
import edu.ncsu.zybook.security.SecurityConfig;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.service.IUserRegistersCourseService;
import edu.ncsu.zybook.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.ui.Model;

import java.sql.Timestamp;

@Controller
@RequestMapping("/enrollment")
public class UserRegistersCourseController {
    IUserService userService;
    ICourseService courseService;
    IUserRegistersCourseService userRegistersCourseService;
    UserWeakDTOMapper userWeakDTOMapper;
    SecurityConfig securityConfig;

    public UserRegistersCourseController(IUserService userService, ICourseService courseService, IUserRegistersCourseService userRegistersCourseService, UserWeakDTOMapper userWeakDTOMapper, SecurityConfig securityConfig) {
        this.userService = userService;
        this.courseService = courseService;
        this.userRegistersCourseService = userRegistersCourseService;
        this.userWeakDTOMapper = userWeakDTOMapper;
        this.securityConfig = securityConfig;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userWeakDTO", new UserWeakDTO());
        return "enrollment/create";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserWeakDTO userWeakDTO, Model model) {
        // Check if user exists by email
        Optional<User> user = userService.findByEmail(userWeakDTO.getEmail());
        UserRegistersCourse registration = new UserRegistersCourse();

        if (user.isPresent()) {
            User registeruser = user.get();

            registration.setUserId(registeruser.getUserId());
            String courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
            registration.setCourseId(courseId);
            registration.setCourseToken(userWeakDTO.getCourseToken());
            registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
            registration.setApprovalStatus("Waiting");

            if (userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId())<userRegistersCourseService.getCapacity(userWeakDTO.getCourseId())) {
                registration.setUserId(registeruser.getUserId());
                courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
                registration.setCourseId(courseId);
                registration.setCourseToken(userWeakDTO.getCourseToken());
                registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
                registration.setApprovalStatus("Waiting");
            }
            else if (userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId())==0){
                registration.setUserId(registeruser.getUserId());
                courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
                registration.setCourseId(courseId);
                registration.setCourseToken(userWeakDTO.getCourseToken());
                registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
                registration.setApprovalStatus("Waiting");
                //return "redirect:/landing/student";
            }
            else{
                return "enrollment/error";
            }
        }
        else{
            User newuser = new User();
            newuser.setFname(userWeakDTO.getFname());
            newuser.setLname(userWeakDTO.getLname());
            newuser.setEmail(userWeakDTO.getEmail());
            PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
            newuser.setPassword(passwordEncoder.encode(userWeakDTO.getPassword()));
            newuser.setRoleName("student");
            //newuser.setUserId(2);
            User createdUser = userService.create(newuser);

            registration.setUserId(createdUser.getUserId());
            String courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
            registration.setCourseId(courseId);
            registration.setCourseToken(userWeakDTO.getCourseToken());
            registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
            registration.setApprovalStatus("Waiting");

//            System.out.println("Current count "+ userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId()));
//            System.out.println(userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId())

            if (userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId())<userRegistersCourseService.getCapacity(userWeakDTO.getCourseId())) {
                registration.setUserId(createdUser.getUserId());
                courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
                registration.setCourseId(courseId);
                registration.setCourseToken(userWeakDTO.getCourseToken());
                registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
                registration.setApprovalStatus("Waiting");
            }
            else if (userRegistersCourseService.getCurrentCount(userWeakDTO.getCourseId())==0){
                registration.setUserId(createdUser.getUserId());
                courseId = userRegistersCourseService.getCourseId(userWeakDTO.getCourseToken());
                registration.setCourseId(courseId);
                registration.setCourseToken(userWeakDTO.getCourseToken());
                registration.setEnrollmentDate(new Timestamp(System.currentTimeMillis()));
                registration.setApprovalStatus("Waiting");
                //return "redirect:/landing/student";
            }
            else{
                return "enrollment/error";
            }
        }

        userRegistersCourseService.create(registration);
        model.addAttribute("message", "Enrollment successful!");

        return "redirect:/landing/student";
    }

    @GetMapping("/students")
    public String getAllStudents(@RequestParam String courseId, Model model) {
        List<User> students = userRegistersCourseService.getAllStudents(courseId);
        model.addAttribute("students", students);
        return "enrollment/students";
    }


//    @GetMapping("/{id}")
//    public String getAllCourses(@PathVariable int id, Model model) {
//        List <UserRegistersCourse> courses = userRegistersCourseService.findAllByUser(id);
//        model.addAttribute("courses", courses);
//        return "enrollments/list";
//    }
//
//    @GetMapping("/{userId}/{courseId}")
//    public String getEnrollment(@PathVariable int userId, @PathVariable String courseId, Model model) {
//        Optional<UserRegistersCourse> userRegistersCourse = userRegistersCourseService.findById(userId, courseId);
//        if (userRegistersCourse.isPresent()) {
//            model.addAttribute("enrollment", userRegistersCourse.get());
//            return "enrollments/enrollment";
//        } else{
//            return "enrollments/not-found";
//        }
//    }

    @GetMapping("/courses")
    public String getCoursesForStudents(@RequestParam("userId") int userId, Model model) {
        List<UserRegistersCourse> userRegistersCourses = userRegistersCourseService.findAllByUser(userId);
        List<Course> enrolledCourses= new ArrayList<>();
        for(UserRegistersCourse userRegistersCourse : userRegistersCourses){
            Optional<Course> result = courseService.findById(userRegistersCourse.getCourseId());
            if(result.isPresent()){
                enrolledCourses.add(result.get());
            }
        }
        System.out.println("Enrolled courses for student:"+enrolledCourses.toString());
        model.addAttribute("courses", enrolledCourses);
        return "course/list";
    }
}
