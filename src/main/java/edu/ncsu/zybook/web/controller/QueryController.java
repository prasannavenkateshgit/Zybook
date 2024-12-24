package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.ActiveCourseInfoDTO;
import edu.ncsu.zybook.DTO.CourseUserDTO;
import edu.ncsu.zybook.DTO.CourseWaitingListDTO;
import edu.ncsu.zybook.persistence.repository.ICourseRepository;
import edu.ncsu.zybook.persistence.repository.ISectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller()
@RequestMapping("/query")
public class QueryController {

    ICourseRepository courseRepository;
    ISectionRepository sectionRepository;

    public QueryController(ICourseRepository courseRepository, ISectionRepository sectionRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping("/countsections")
    public String sectionsInChap(
            @RequestParam("tbookId" ) int tbookId,
            @RequestParam("chapId" ) int chapId,
            Model model
            ){
        Integer count = sectionRepository.countOfSectionsInChapPerTbook(tbookId,chapId);
        model.addAttribute("tbookId", tbookId);
        model.addAttribute("chapId", chapId);
        model.addAttribute("count", count);
        return "query/countsections";
    }

    @GetMapping("/staff")
    public String courseStaff(Model model){
        List<CourseUserDTO> staff = courseRepository.findFacultyAndTAsForCourses();
        model.addAttribute("staff", staff);
        return "query/staff";
    }

    @GetMapping("/activestrength")
    public String activeCourseStrength(Model model){
        List<ActiveCourseInfoDTO> courseInfoDTOS = courseRepository.findActiveCoursesWithFacultyAndStudentCount();
        model.addAttribute("courseInfoDTOs", courseInfoDTOS);
        return "query/activestrength";
    }

    @GetMapping("/lwaiting")
    public String largestWaitingCourse(Model model){
        Optional<CourseWaitingListDTO> courseWaitingListDTO = courseRepository.findCourseWithLargestWaitingList();
        courseWaitingListDTO.ifPresent(waitingListDTO -> model.addAttribute("waitinglist", waitingListDTO));
        return "query/lwaiting";
    }

}
