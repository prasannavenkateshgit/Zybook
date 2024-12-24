package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;

import java.util.List;
import java.util.Optional;

public interface ICourseService {
    Course create(Course entity);
    Optional<Course> update(Course entity);
    boolean delete(String id);
    Optional<Course> findById(String id);
    List<Course> findAll();
    Optional<Course> findByTitle(String title);
    Optional<Course> updateProfessor(Course course, int professorId);
    List<ActiveCourse> getActiveCourses(int professorId, String role);
    List<Course> getEvaluationCourse(int professorId, String role);
    List<Course> getAllCoursesForUser(int userId);
    int getTbookId(String courseId);
}
