package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.UserRegistersCourse;
import edu.ncsu.zybook.domain.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRegistersCourseRepository implements IUserRegistersCourseRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public UserRegistersCourse create(UserRegistersCourse userRegistersCourse) {
        String sql = "INSERT INTO UserRegistersCourse (user_id, CourseId, enrollment_date, approval_status) VALUES(?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, userRegistersCourse.getUserId(), userRegistersCourse.getCourseId(), userRegistersCourse.getEnrollmentDate(), userRegistersCourse.getApprovalStatus());
        if (rowsAffected > 0) {
            return findById(userRegistersCourse.getUserId(), userRegistersCourse.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted UserRegistersCourse."));
        } else {
            throw new RuntimeException("Failed to insert UserRegistersCourse.");
        }
    }

    @Override
    public Optional<UserRegistersCourse> findById(int userId, String courseId) {
        String sql = "SELECT * FROM UserRegistersCourse WHERE user_id = ? AND CourseId = ?";
        try {
            UserRegistersCourse userRegistersCourse = jdbcTemplate.queryForObject(sql, new Object[]{userId, courseId}, new UserRegistersCourseRepository.UserRegistersCourseRowMapper());
            return Optional.of(userRegistersCourse);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserRegistersCourse> update(UserRegistersCourse userRegistersCourse) {
        String sql = "UPDATE UserRegisterCourse SET approval_status = ? WHERE user_id = ? AND courseId = ?";
        int rowsAffected = jdbcTemplate.update(sql, userRegistersCourse.getApprovalStatus(), userRegistersCourse.getUserId(), userRegistersCourse.getCourseId());
        return rowsAffected > 0 ? Optional.of(userRegistersCourse) : Optional.empty();
    }

    @Override
    public boolean delete(int userId, String courseId) {
        String sql = "DELETE FROM UserRegistersCourse WHERE user_id = ? AND courseId = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, courseId);
        return rowsAffected > 0;
    }

    @Override
    public List<UserRegistersCourse> findAllByUser(int userId) {
        String sql = "SELECT * FROM UserRegistersCourse WHERE user_id = ? ORDER BY enrollment_date DESC";
        return jdbcTemplate.query(sql, new Object[]{userId}, new UserRegistersCourseRepository.UserRegistersCourseRowMapper());
    }

    @Override
    public int getCurrentCount(String courseId) {
        String sql = "SELECT COUNT(*) FROM UserRegistersCourse WHERE CourseID = ? AND approval_status != 'Rejected'";
        try {
            //int currcount = jdbcTemplate.update(sql, courseId);
            int currcount = jdbcTemplate.queryForObject(sql, new Object[]{courseId}, Integer.class);
            return currcount;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public int getCapacity(String courseId) {
        String sql = "SELECT course_capacity from ActiveCourse WHERE course_id = ?";
        try{
            //int capacity = jdbcTemplate.update(sql, courseId);
            int capacity = jdbcTemplate.queryForObject(sql, new Object[]{courseId}, Integer.class);
            return capacity;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public String getCourseId(String courseToken){
        String sql = "SELECT course_id FROM ActiveCourse WHERE course_token = ?";
        try{
            String courseId = jdbcTemplate.queryForObject(sql, new Object[]{courseToken}, String.class);
            return courseId;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<User> getAllStudents(String courseId) {
        String sql = "SELECT u.user_id, u.fname, u.lname, u.email from User u INNER JOIN UserRegistersCourse urc WHERE u.user_id = urc.user_id AND urc.CourseID = ? AND urc.approval_status = 'Approved'";
        return jdbcTemplate.query(sql, new Object[]{courseId}, new UserRegistersCourseRepository.StudentsCourseRowMapper());
    }

    private static class StudentsCourseRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException{
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setFname(rs.getString("fname"));
            user.setLname(rs.getString("lname"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }

    private static class UserRegistersCourseRowMapper implements RowMapper<UserRegistersCourse> {
        @Override
        public UserRegistersCourse mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserRegistersCourse userRegistersCourse = new UserRegistersCourse();
            userRegistersCourse.setUserId(rs.getInt("user_id"));
            userRegistersCourse.setCourseId(rs.getString("courseId"));
            userRegistersCourse.setEnrollmentDate(rs.getObject("enrollment_date", Timestamp.class));
            userRegistersCourse.setApprovalStatus(rs.getString("approval_status"));
            return userRegistersCourse;
        }
    }
}
