package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.persistence.repository.ICourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.service.ICourseService;
import edu.ncsu.zybook.persistence.repository.ICourseRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ActiveCourseRepository implements IActiveCourseRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;
    //ICourseService courseService;
    ICourseRepository courseRepository = new CourseRepository();

    @Transactional
    @Override
    public ActiveCourse create(ActiveCourse activeCourse) {
        Course course = new Course();
        course.setCourseId(activeCourse.getCourseId());
        course.setTitle(activeCourse.getTitle());
        course.setStartDate(activeCourse.getStartDate());
        course.setEndDate(activeCourse.getEndDate());
        course.setCourseType(activeCourse.getCourseType());
        course.setTbookId(activeCourse.getTbookId());
        course.setProfessorId(activeCourse.getProfessorId());
        //Course created = courseRepository.create(course);
        String sql = "INSERT INTO Course (course_id, title, start_date, end_date, course_type, textbook_id, professor_id) VALUES(?,?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql, course.getCourseId(), course.getTitle(), course.getStartDate(), course.getEndDate(), course.getCourseType(), course.getTbookId(), course.getProfessorId());
        System.out.println("Course created");

        String sql2 = "INSERT INTO ActiveCourse (course_token, course_capacity, course_id) VALUES (?, ?, ?)";
        int rowsAffected2 = jdbcTemplate.update(sql2, activeCourse.getCourseToken(), activeCourse.getCourseCapacity(), activeCourse.getCourseId());
        System.out.println("Rows affected: " + rowsAffected2);
        System.out.println("Active course created");
        if (rowsAffected2 > 0) {
            return findById(activeCourse.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted active course."));
        } else {
            throw new RuntimeException("Failed to insert active course.");
        }
    }

    @Override
    public Optional<ActiveCourse> findById(String courseId) {
        String sql = "SELECT * FROM ActiveCourse WHERE course_id = ?";
        try {
            ActiveCourse activeCourse = jdbcTemplate.queryForObject(sql, new Object[]{courseId}, new ActiveCourseRowMapper());
            return Optional.of(activeCourse);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Optional<ActiveCourse> update(ActiveCourse activeCourse) {
        String sql = "UPDATE ActiveCourse SET course_token = ?, course_capacity = ? WHERE course_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, activeCourse.getCourseToken(), activeCourse.getCourseCapacity(), activeCourse.getCourseId());
        return rowsAffected > 0 ? Optional.of(activeCourse) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean delete(String courseId) {
        String sql = "DELETE FROM ActiveCourse WHERE course_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, courseId);
        return rowsAffected > 0;
    }

    private static class ActiveCourseRowMapper implements RowMapper<ActiveCourse> {
        @Override
        public ActiveCourse mapRow(ResultSet rs, int rowNum) throws SQLException {
            ActiveCourse activeCourse = new ActiveCourse();
            activeCourse.setCourseId(rs.getString("course_id"));
            activeCourse.setCourseToken(rs.getString("course_token"));
            activeCourse.setCourseCapacity(rs.getInt("course_capacity"));
            return activeCourse;
        }
    }
}
