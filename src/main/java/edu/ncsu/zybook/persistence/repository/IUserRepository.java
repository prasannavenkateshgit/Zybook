package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Notification;
import edu.ncsu.zybook.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    User create(User user);
    Optional<User> update(User user);
    boolean delete(int id);
    List<User> getAllUsers();
    List<User> getWaitingList(String courseId);
    boolean approve(String courseId, int userId);
    boolean reject(String courseId, int userId);
    List<Notification> getNotification(int userId);
    String getCurrentPassword(User user);
    User createTA(User user, String courseId);
    String getUserRole(int userId);
}
