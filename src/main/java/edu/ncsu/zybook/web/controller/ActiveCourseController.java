package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.service.IActiveCourseService;
import edu.ncsu.zybook.service.ITextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/activecourse")
public class ActiveCourseController {
    //ICourseService courseService;
//    ITextbookService textbookService;
    IActiveCourseService activeCourseService;
    ICourseService courseService;
    ITextbookService textbookService;

    public ActiveCourseController(IActiveCourseService activeCourseService, ICourseService courseService, ITextbookService textbookService) {
        this.activeCourseService = activeCourseService;
        this.courseService = courseService;
        this.textbookService = textbookService;
    }

    //    @GetMapping("/{id}")
//    public String getActiveCourse(@PathVariable String id, Model model) {
//        Optional<ActiveCourse> coursevariable = activeCourseService.findById(id);
//        if (coursevariable.isPresent()) {
//            ActiveCourse activeCourse = coursevariable.get();
//
//            Optional<Textbook> textbook= textbookService.findById(activeCourse.getTbookId());
//            if (textbook.isPresent())
//            {model.addAttribute("textbook", textbook.get());
//            }
//            else {model.addAttribute("textbook", null);
//            }
//            model.addAttribute("activecourse", activeCourse);
//            return "activecourse/course";
//        }else {
//            return "activecourse/not-found";
//        }
//    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("activecourse", new ActiveCourse());
        List<Integer> inUseTbooks = courseService.findAll().stream().map(Course::getTbookId).toList();
        List<Integer> tbooks = textbookService.getAllTextbooks(0,100,"uid","ASC").stream().map(Textbook::getUid).toList();
        List<Integer> unUsedTbookIds = new ArrayList<>(tbooks);
        unUsedTbookIds.removeAll(inUseTbooks);
        List<Textbook> unUsedTbooks = unUsedTbookIds.stream()
                .map(textbookService::findById)
                .flatMap(Optional::stream)
                .toList();
        model.addAttribute("unUsedTbooks", unUsedTbooks);
        return "activecourse/create";
    }

    @PostMapping("/registeractive")
    public String createActiveCourse(@ModelAttribute ActiveCourse activeCourse) {
        System.out.println(activeCourse);
        activeCourseService.create(activeCourse);
        return "redirect:/courses";
    }

//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable String id, Model model) {
//        Optional<Course> course = courseService.findById(id);
//        if (course.isPresent()) {
//            model.addAttribute("course", course.get());
//            return "course/create";
//        } else {
//            return "course/not-found";
//        }
//    }

//    @PutMapping("/update")
//    public String updateCourse(@ModelAttribute Course course) {
//        System.out.println(course);
//        courseService.update(course);
//        return "redirect:/courses";
//    }

//    @DeleteMapping("/{id}")
//    public String deleteCourse(@PathVariable String id) {
//        courseService.delete(id);
//        return "redirect:/courses";
//    }
//
//    @GetMapping
//    public String getAllCourses(Model model) {
//        List<Course> courses = courseService.findAll();
//        model.addAttribute("courses", courses);
//        return "course/list";
//    }
}
