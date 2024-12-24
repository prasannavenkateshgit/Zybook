package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.DTO.CourseUserDTO;
import edu.ncsu.zybook.DTO.CourseWaitingListDTO;
import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.DTO.ActiveCourseInfoDTO;

import java.util.List;
import java.util.Optional;

public interface ICourseRepository {
    Course create(Course entity);
    Optional<Course> update(Course entity);
    boolean delete(String id);
    Optional<Course> findById(String id);
    List<Course> findAll();
    Optional<Course> findByTitle(String title);
    Optional<Course> updateProfessor(Course course, int professorId);
    List<ActiveCourse> getActiveCourse(int professorId, String role);
    List<Course> getEvaluationCourse(int professorId, String role);
    List<Course> getAllCoursesForUser(int userId);
    int getTbookId(String courseId);
    List<CourseUserDTO> findFacultyAndTAsForCourses();
    List<ActiveCourseInfoDTO> findActiveCoursesWithFacultyAndStudentCount();
    Optional<CourseWaitingListDTO> findCourseWithLargestWaitingList();
}
