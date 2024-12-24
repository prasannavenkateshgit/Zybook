package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Notification;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.domain.model.User;
import edu.ncsu.zybook.persistence.repository.IUserRepository;
import edu.ncsu.zybook.persistence.repository.UserRepository;
import edu.ncsu.zybook.service.IUserService;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
@Service
public class UserService implements IUserService {
    private IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User create(User user) {
        Optional<User> result= userRepository.findByEmail(user.getEmail());
        if(result.isEmpty()) {
            return userRepository.create(user);
        }
        else {
            throw new RuntimeException("User already exists");
        }
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> result = userRepository.findById(id);
        if(result.isPresent()) {
            User user = result.get();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        if(result.isPresent()) {
            User user = result.get();
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        if(userRepository.findById(user.getUserId()).isPresent())
            return userRepository.update(user);
        else
            throw new RuntimeException("There is no User with this id");
    }

    @Override
    public boolean delete(int id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.delete(id);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public List<User> getWaitingList(String courseId){
        return userRepository.getWaitingList(courseId);
    }

    @Override
    public boolean approve(String courseId, int userId) {
        boolean result = userRepository.approve(courseId, userId);
        return result;
    }

    @Override
    public boolean reject(String courseId, int userId) {
        boolean result = userRepository.reject(courseId, userId);
        return result;
    }

    @Override
    public List<Notification> getNotification(int userId) {
        return userRepository.getNotification(userId);
    }

    @Override
    public String getPassword(User user) {
        return userRepository.getCurrentPassword(user);
    }

    @Override
    public String getUserRole(int userId) {
        return userRepository.getUserRole(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User createTA(User user, String courseId){
        return userRepository.createTA(user, courseId);
    }
}
