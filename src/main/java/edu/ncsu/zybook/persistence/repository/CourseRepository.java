package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.DTO.CourseUserDTO;
import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.DTO.ActiveCourseInfoDTO;
import edu.ncsu.zybook.DTO.CourseWaitingListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CourseRepository implements ICourseRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Course> findByTitle(String title) {
        String sql = "SELECT * FROM course WHERE title = ?";
        try{
            Course course = jdbcTemplate.queryForObject(sql, new Object[]{title}, new CourseRepository.CourseRowMapper());
            return Optional.of(course);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Transactional
    @Override
    public Course create(Course course) {
        System.out.println("CREEE" + course);
        String sql = "INSERT INTO Course (course_id, title, start_date, end_date, course_type, textbook_id, professor_id) VALUES(?,?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql, course.getCourseId(), course.getTitle(), course.getStartDate(), course.getEndDate(), course.getCourseType(), course.getTbookId(), course.getProfessorId());
        if(rowsAffected > 0)
        {
            return findById(course.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted Course."));
        }
        else{
            throw new RuntimeException("Failed to insert Course.");
        }
    }

    @Override
    public Optional<Course> findById(String courseId) {
        System.out.println("DEBUGGGGG "+ courseId);
        String sql = "SELECT * FROM Course WHERE course_id = ?";
        try {
            Course course = jdbcTemplate.queryForObject(sql, new Object[]{courseId}, new CourseRowMapper());

            System.out.println("DEBUGGGGG 1234s"+course.getCourseId());
            return Optional.of(course);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Optional<Course> update(Course course) {
        System.out.println("UPDATE "+ course);
        String sql = "UPDATE Course SET title = ?, start_date = ?, end_date = ?, course_type = ?, textbook_id = ?,  professor_id = ? WHERE course_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, course.getTitle(), course.getStartDate(), course.getEndDate(), course.getCourseType(), course.getTbookId(), course.getProfessorId(), course.getCourseId());
        return rowsAffected > 0 ? Optional.of(course) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean delete(String courseId) {
        String sql = "DELETE FROM Course WHERE course_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, courseId);
        return rowsAffected > 0;
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT * FROM Course";
        return jdbcTemplate.query(sql, new CourseRowMapper());
    }
    public Optional<Course> updateProfessor(Course course, int professorId) {
        String sql = "UPDATE Course SET professor_id = ? WHERE course_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, professorId, course.getCourseId());
        Optional<Course> updatedCourse = findById(course.getCourseId());
        if(rowsAffected > 0){
            return updatedCourse;
        }
        return Optional.empty();
    }

    @Override
    public List<ActiveCourse> getActiveCourse(int userId, String role) {
        if(role.equalsIgnoreCase("faculty")){
            String sql = "SELECT * FROM Course WHERE professor_id = ? AND course_type = 'ACTIVE'";
            List<ActiveCourse> courses = jdbcTemplate.query(sql, new Object[]{userId}, new CourseRowMapper()).stream().map(e->((ActiveCourse) e)).collect(Collectors.toList());
            System.out.println("In repository:"+courses.toString());
            return courses;
        }
       else if(role.equalsIgnoreCase("ta")){
           String sql = "SELECT C.course_id course_id, C.title title, C.start_date start_date, C.end_date end_date, C.course_type course_type, C.textbook_id textbook_id, C.professor_id professor_id FROM Assigned A, Course C WHERE A.course_id = C.course_id AND A.user_id=? AND course_type = 'ACTIVE'";
           List<ActiveCourse> courses = jdbcTemplate.query(sql, new Object[]{userId}, new CourseRowMapper()).stream().map(e->((ActiveCourse) e)).collect(Collectors.toList());
           System.out.println("In repository for TA:"+courses.toString());
           return courses;
       }
       return null;
    }

    @Override
    public List<CourseUserDTO> findFacultyAndTAsForCourses() {
        String sql = """
        SELECT c.title AS course_title,
           u.fname AS first_name,
           u.lname AS last_name,
           CASE
               WHEN u.role_name = 'ta' THEN 'TA'
               WHEN u.role_name = 'faculty' THEN 'Faculty'
           END AS role
        FROM Course c
        LEFT JOIN Assigned a ON c.course_id = a.course_id
        LEFT JOIN User u ON (a.user_id = u.user_id AND u.role_name = 'ta')
            OR (c.professor_id = u.user_id AND u.role_name = 'faculty')
        WHERE u.role_name IN ('ta', 'faculty')
        ORDER BY c.title;
            
        """;

        return jdbcTemplate.query(sql, courseUserRowMapper());
    }

    @Override
    public List<ActiveCourseInfoDTO> findActiveCoursesWithFacultyAndStudentCount() {
        String sql = """
            SELECT c.course_id,
                CONCAT(u.fname, ' ', u.lname) AS faculty_name,
                COUNT(urc.user_id) AS total_students
            FROM Course c
            LEFT JOIN User u ON c.professor_id  = u.user_id AND u.role_name = 'faculty'
            LEFT JOIN UserRegistersCourse urc ON c.course_id = urc.CourseID AND urc.approval_status = 'Approved'
            WHERE c.course_type = 'Active'
            GROUP BY c.course_id, faculty_name
            ORDER BY c.course_id
        """;

        return jdbcTemplate.query(sql, activeCourseInfoRowMapper());
    }

    @Override
    public Optional<CourseWaitingListDTO> findCourseWithLargestWaitingList() {
        String sql = """
            SELECT urc.CourseID AS course_id,
                COUNT(urc.user_id) AS waiting_list_count
            FROM UserRegistersCourse urc
            WHERE urc.approval_status = 'Waiting'
            GROUP BY urc.CourseID
            ORDER BY waiting_list_count DESC
            LIMIT 1
        """;

        List<CourseWaitingListDTO> result = jdbcTemplate.query(sql, waitingListRowMapper());
        return result.stream().findFirst();
    }

    @Override
    public List<Course> getEvaluationCourse(int professorId, String role) {
        if(role.equalsIgnoreCase("faculty")){
            String sql = "SELECT * FROM Course WHERE professor_id = ? AND course_type = 'EVALUATION'";
            List<Course> evaluationCourses = jdbcTemplate.query(sql, new Object[]{professorId}, new CourseRowMapper());
            return evaluationCourses;
        }
        else if(role.equalsIgnoreCase("ta")){
            String sql = "SELECT C.course_id course_id, C.title title, C.start_date start_date, C.end_date end_date, C.course_type course_type, C.textbook_id textbook_id, C.professor_id professor_id FROM Assigned A, Course C WHERE A.course_id = C.course_id AND A.user_id=? AND course_type = 'EVALUATION'";
            List<Course> courses = jdbcTemplate.query(sql, new Object[]{professorId}, new CourseRowMapper());
            System.out.println("In repository for TA:"+courses.toString());
            return courses;
        }
        return null;

    }

    @Override
    public List<Course> getAllCoursesForUser(int userId) {
        String sql = "SELECT * FROM Course WHERE professor_id = ?";
        List<Course> courses = jdbcTemplate.query(sql, new Object[]{userId}, new CourseRowMapper());
        return courses;
    }

    @Override
    public int getTbookId(String courseId){
        String sql = "SELECT textbook_id FROM Course WHERE course_id = ?";
        try{
            int tbookId = jdbcTemplate.queryForObject(sql, new Object[]{courseId}, Integer.class);
            return tbookId;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    private class CourseRowMapper implements RowMapper<Course> {
        @Override
        public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
            Course course;
            if(rs.getString("course_type").equalsIgnoreCase("ACTIVE"))
                course = new ActiveCourse();
            else course = new Course();
            course.setCourseId(rs.getString("course_id"));
            course.setTitle(rs.getString("title"));
            course.setStartDate(rs.getDate("start_date"));
            course.setEndDate(rs.getDate("end_date"));
            course.setCourseType(rs.getString("course_type"));
            course.setTbookId(rs.getInt("textbook_id"));
            course.setProfessorId(rs.getInt("professor_id"));
            if(rs.getString("course_type").equalsIgnoreCase("ACTIVE"))
                fetchActiveCourseDetails(course);
            return course;
        }
    }

    private void fetchActiveCourseDetails(Course course) {
        String sql = "SELECT * FROM ActiveCourse WHERE course_id = ?";
        try{
            ActiveCourse result = jdbcTemplate.queryForObject(sql, new Object[]{course.getCourseId()}, new RowMapper<ActiveCourse>() {
                @Override
                public ActiveCourse mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ((ActiveCourse) course).setCourseCapacity(rs.getInt("course_capacity"));
                    ((ActiveCourse) course).setCourseToken(rs.getString("course_token"));
                    return ((ActiveCourse) course);
                }
            });

            ((ActiveCourse) course).setCourseCapacity(result.getCourseCapacity());
            ((ActiveCourse) course).setCourseToken(result.getCourseToken());
        }
        catch(EmptyResultDataAccessException e){
            System.out.println("No active course found with course_id: " + course.getCourseId());
        }
    }

    private RowMapper<CourseUserDTO> courseUserRowMapper() {
        return (rs, rowNum) -> new CourseUserDTO(
                rs.getString("course_title"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("role")
        );
    }

    private RowMapper<ActiveCourseInfoDTO> activeCourseInfoRowMapper() {
        return (rs, rowNum) -> new ActiveCourseInfoDTO(
                rs.getString("course_id"),
                rs.getString("faculty_name"),
                rs.getInt("total_students")
        );
    }

    private RowMapper<CourseWaitingListDTO> waitingListRowMapper() {
        return (rs, rowNum) -> new CourseWaitingListDTO(
                rs.getString("course_id"),
                rs.getInt("waiting_list_count")
        );
    }

}
