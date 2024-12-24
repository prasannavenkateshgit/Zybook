package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.persistence.repository.ICourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements ICourseService {

    private final ICourseRepository courseRepository;

    public CourseService(ICourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    @Override
    public Course create(Course course) {
        Optional<Course> result = courseRepository.findById(course.getCourseId());
        if (result.isEmpty()) {
            return courseRepository.create(course);
        }
        else{
            throw new RuntimeException("Course already exists!");
        }
    }

    @Override
    @Transactional
    public Optional<Course> update(Course course) {
//        System.out.println("DEBBBBUG 0987"+course.getCourseId());
        if(courseRepository.findById(course.getCourseId()).isPresent()){
//            System.out.println("DEBBBBUG ISIDE IF");
            return courseRepository.update(course);
        }
        else{
            throw new RuntimeException("Course does not exist with id:" + course.getCourseId());
        }
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        Optional<Course> result = courseRepository.findById(id);
        if (result.isPresent()) {
            return courseRepository.delete(id);
        }
        else{
            throw new RuntimeException("Course does not exist with id:" + id);
        }
    }

    @Override
    public Optional<Course> findById(String id) {
        Optional<Course> result = courseRepository.findById(id);
        if(result.isPresent()) {
            Course course = result.get();
            //List<Section> sections = sectionRepository.findAllByChapter(tbookId, cno);
            return Optional.of(course);
        }
        return Optional.empty();
    }

    @Override
    public List<Course> findAll() {
        List<Course> result = courseRepository.findAll();
        return result;
    }

    @Override
    public Optional<Course> findByTitle(String title) {
        Optional<Course> result = courseRepository.findByTitle(title);
        if(result.isPresent()) {
            Course course = result.get();
            return Optional.of(course);
        }
        else{
            throw new RuntimeException("Course does not exist with title:" + title);
        }
    }

    @Override
    public Optional<Course> updateProfessor(Course course, int professorId) {
        if(courseRepository.findById(course.getCourseId()).isPresent()){
            Optional<Course> result = courseRepository.updateProfessor(course,professorId);
            return result;
        }
        return Optional.empty();
    }

    @Override
    public List<ActiveCourse> getActiveCourses(int professorId, String role) {
        List<ActiveCourse> activeCourses = courseRepository.getActiveCourse(professorId, role);
        System.out.println("In service:"+activeCourses.toString());
        return activeCourses;
    }

    @Override
    public List<Course> getEvaluationCourse(int professorId, String role) {
        List<Course> evaluationCourses = courseRepository.getEvaluationCourse(professorId, role);
        System.out.println("In service:"+evaluationCourses.toString());
        return evaluationCourses;
    }

    @Override
    public List<Course> getAllCoursesForUser(int userId) {
        return courseRepository.getAllCoursesForUser(userId);
    }

    @Override
    public int getTbookId(String courseId) {
        return courseRepository.getTbookId(courseId);
    }
}
