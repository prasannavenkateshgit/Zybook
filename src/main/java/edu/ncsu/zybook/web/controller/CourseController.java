package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.service.ITextbookService;
import edu.ncsu.zybook.service.IUserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final ICourseService courseService;
    private final ITextbookService textbookService;
    private final IUserService userService;

    public CourseController(ICourseService courseService, ITextbookService textbookService, IUserService userService) {
        this.courseService = courseService;
        this.textbookService = textbookService;
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public String getCourse(@PathVariable String id, @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        Optional<Course> coursevariable = courseService.findById(id);
        if (coursevariable.isPresent()) {
            Course course = coursevariable.get();

            Optional<Textbook> textbook= textbookService.findById(course.getTbookId());
            if (textbook.isPresent())
            {model.addAttribute("textbook", textbook.get());
            }
            else {model.addAttribute("textbook", null);
            }
            model.addAttribute("course", course);
            model.addAttribute("userId",customUserDetails.getId());
            return "course/course";
        }else {
            return "course/not-found";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        List<Integer> inUseTbooks = courseService.findAll().stream().map(Course::getTbookId).toList();
        List<Integer> tbooks = textbookService.getAllTextbooks(0,100,"uid","ASC").stream().map(Textbook::getUid).toList();
        List<Integer> unUsedTbookIds = new ArrayList<>(tbooks);
        unUsedTbookIds.removeAll(inUseTbooks);
        List<Textbook> unUsedTbooks = unUsedTbookIds.stream()
                .map(textbookService::findById)
                .flatMap(Optional::stream)
                .toList();
        model.addAttribute("unUsedTbooks", unUsedTbooks);
        return "course/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public String createCourse(@ModelAttribute Course course) {
        courseService.create(course);
        return "redirect:/courses";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Optional<Course> course = courseService.findById(id);
        if (course.isPresent()) {
            model.addAttribute("course", course.get());
            return "course/create";
        } else {
            return "course/not-found";
        }
    }

    @PreAuthorize("hasAnyRole( 'ADMIN')")
    @PutMapping("/update")
    public String updateCourse(@ModelAttribute Course course) {
        System.out.println(course);
        courseService.update(course);
        return "redirect:/courses";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable String id) {
        courseService.delete(id);
        return "redirect:/courses";
    }

    @GetMapping
    public String getAllCourses(Model model) {
        System.out.println("Inside get all courses controller");
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);
        return "course/list";
    }
    @GetMapping("/active")
    public String getActiveCourse(@RequestParam("userId") int userId, Model model) {
        String role = userService.getUserRole(userId);
        if (role.equals("")) {
            throw new RuntimeException("No suitbale role found for the user to get active courses");
        }
        List<ActiveCourse> activeCourses = courseService.getActiveCourses(userId,role);
        System.out.println(activeCourses);
        model.addAttribute("userId",userId);
        model.addAttribute("courses", activeCourses);
        return "course/activelist";
    }
    @GetMapping("/evaluation")
    public String getEvaluationCourse(@RequestParam("userId") int userId, Model model) {
        String role = userService.getUserRole(userId);
        if (role.equals("")) {
            throw new RuntimeException("No suitable role found for the user to get eval courses");
        }
        List<Course> evaluationCourse = courseService.getEvaluationCourse(userId,role);
        model.addAttribute("courses", evaluationCourse);
        return "course/list";
    }
    @GetMapping("/all")
    public String getAllCoursesForUser(@RequestParam("userId") int userId, Model model) {
//        List<Course> courses = courseService.getAllCoursesForUser(userId);
        String role = userService.getUserRole(userId);
        if(role.equalsIgnoreCase("ADMIN")){
            List<Course> courses = courseService.getAllCoursesForUser(userId);
            model.addAttribute("courses", courses);
            return "course/list";
        }
        List<Course> courses = courseService.getActiveCourses(userId,role).stream().map(e->(Course)e).collect(Collectors.toList());
        courses.addAll(courseService.getEvaluationCourse(userId,role));
        model.addAttribute("courses", courses);
        return "course/list";
    }
}