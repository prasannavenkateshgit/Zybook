package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Notification;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM User WHERE user_id = ?";
        try{
            User user = jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRepository.UserRowMapper());
            return Optional.of(user);
        }
        catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";
        try{
            User user = jdbcTemplate.queryForObject(sql, new Object[]{email}, new UserRepository.UserRowMapper());
            return Optional.of(user);
        }
        catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO User (fname,lname,email,password,role_name) VALUES(?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql, user.getFname(), user.getLname(), user.getEmail(), user.getPassword(), user.getRoleName());
        if(rowsAffected > 0)
        {
            return findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted User."));
        }
        else{
            throw new RuntimeException("Failed to insert User.");
        }
    }

    @Override
    public User createTA(User user, String courseId){
        String sql = "INSERT INTO User (fname,lname,email,password,role_name) VALUES(?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql, user.getFname(), user.getLname(), user.getEmail(), user.getPassword(), user.getRoleName());
        Optional<User> result = findByEmail(user.getEmail());
        if(result.isPresent()){
            User createdUser = result.get();
            String sql2 = "INSERT INTO Assigned (course_id,user_id) VALUES(?,?)";
            int rowsAffected2 = jdbcTemplate.update(sql2, courseId, createdUser.getUserId());
            if(rowsAffected > 0)
            {
                return findByEmail(user.getEmail())
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted User."));
            }
            else{
                throw new RuntimeException("Failed to insert User.");
            }
        }
        throw new RuntimeException("Failed to insert User.");
    }

    @Override
    public Optional<User> update(User user) {
        System.out.println("Password correct, going to update now");
        String sql = "UPDATE User SET fname = ?, lname = ?, email = ?, password=? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, user.getFname(), user.getLname(), user.getEmail(), user.getNewPassword(), user.getUserId());
        return rowsAffected > 0 ? Optional.of(user) : Optional.empty();
    }
    public String getCurrentPassword(User user) {
        String sql = "SELECT password FROM user WHERE user_id = ?";
        String password = jdbcTemplate.queryForObject(sql, new Object[]{user.getUserId()}, String.class);
        System.out.println("Current Password:"+password);
        return password;
    }

    @Override
    public String getUserRole(int userId) {
        String sql = "SELECT role_name FROM User WHERE user_id = ?";
        String roleName = jdbcTemplate.queryForObject(sql, new Object[]{userId}, String.class);
        return roleName;
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM User WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM User";
        return jdbcTemplate.query(sql, new UserRepository.UserRowMapper());
    }

    @Override
    public List<User> getWaitingList(String courseId){
        //String sql = "SELECT user_id FROM UserRegistersCourse WHERE approval_status = 'Waiting' AND course_id = ?";
        String sql = "SELECT u.user_id, u.fname, u.lname, u.email, u.password, u.role_name FROM User u INNER JOIN UserRegistersCourse r WHERE u.user_id = r.user_id AND r.approval_status='Waiting' AND r.CourseID=?";

        return jdbcTemplate.query(sql, new Object[]{courseId}, new UserRepository.UserRowMapper());
    }

    @Override
    public boolean approve(String courseId, int userId) {
        String sql = "UPDATE UserRegistersCourse SET approval_status='Approved' WHERE user_id=? AND CourseID=?";
        int rowsAffected = jdbcTemplate.update(sql, userId, courseId);
        if (rowsAffected>0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean reject(String courseId, int userId) {
        String sql = "UPDATE UserRegistersCourse SET approval_status='Rejected' WHERE user_id=? AND CourseID=?";
        int rowsAffected = jdbcTemplate.update(sql, userId, courseId);
        if (rowsAffected>0){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Notification> getNotification(int userId) {
        String sql = "SELECT * FROM Notification WHERE user_id=?";
        //int rowsAffected = jdbcTemplate.update(sql, userId);
        return jdbcTemplate.query(sql, new Object[]{userId}, new UserRepository.NotificationMapper());
    }

    private static class NotificationMapper implements RowMapper<Notification> {
        @Override
        public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
            Notification notification = new Notification();
            notification.setUserId(rs.getInt("user_id"));
            notification.setCourseId(rs.getString("CourseID"));
            notification.setMessage(rs.getString("message"));
            return notification;
        }
    }



    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setFname(rs.getString("fname"));
            user.setLname(rs.getString("lname"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRoleName(rs.getString("role_name"));
            return user;
        }
    }
}

