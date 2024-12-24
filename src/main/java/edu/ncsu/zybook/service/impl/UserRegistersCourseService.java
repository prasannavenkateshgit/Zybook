package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.*;
import edu.ncsu.zybook.persistence.repository.*;
import edu.ncsu.zybook.service.IUserRegistersCourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserRegistersCourseService implements IUserRegistersCourseService {

    private final ICourseRepository courseRepository;
    private final IUserRepository userRepository;
    private final IUserRegistersCourseRepository userRegistersCourseRepository;

    public UserRegistersCourseService(ICourseRepository courseRepository,IUserRepository userRepository,IUserRegistersCourseRepository userRegistersCourseRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.userRegistersCourseRepository = userRegistersCourseRepository;
    }

    @Override
    @Transactional
    public UserRegistersCourse create(UserRegistersCourse userRegistersCourse) {
        Optional<UserRegistersCourse> result= userRegistersCourseRepository.findById(userRegistersCourse.getUserId(), userRegistersCourse.getCourseId());
        if(result.isEmpty()) {
            return userRegistersCourseRepository.create(userRegistersCourse);
        }
        else {
            throw new RuntimeException("UserRegistersCourse already exists");
        }
        //return userRegistersCourseRepository.create(userRegistersCourse);
    }

    @Override
    public Optional<UserRegistersCourse> findById(int userId, String courseId) {
        Optional<UserRegistersCourse> result = userRegistersCourseRepository.findById(userId,courseId);
        if(result.isPresent()) {
            UserRegistersCourse userRegistersCourse =  result.get();
            //List<Content> contents = contentRepository.findAllBySection(sno,chapterId,tbookId); // need to add this to DTO
            return  Optional.of(userRegistersCourse);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<UserRegistersCourse> update(UserRegistersCourse userRegistersCourse) {
        if(userRegistersCourseRepository.findById(userRegistersCourse.getUserId(), userRegistersCourse.getCourseId()).isPresent())
            return userRegistersCourseRepository.update(userRegistersCourse);
        else
            throw new RuntimeException("There is no UserRegistersCourse with  user_id: "+ userRegistersCourse.getUserId()+" and course_id: "+ userRegistersCourse.getCourseId());
    }

    @Override
    @Transactional
    public boolean delete(int userId, String courseId) {
        Optional<UserRegistersCourse> existingUserRegistersCourse = userRegistersCourseRepository.findById(userId,courseId);
        if (existingUserRegistersCourse.isPresent()) {
            userRegistersCourseRepository.delete(userId,courseId);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int getCurrentCount(String courseId) {
        return userRegistersCourseRepository.getCurrentCount(courseId);
    }

    @Override
    public int getCapacity(String courseId) {
        return userRegistersCourseRepository.getCapacity(courseId);
    }

    @Override
    public List<UserRegistersCourse> findAllByUser(int userId) {
        return userRegistersCourseRepository.findAllByUser(userId);
    }

    @Override
    public List<User> getAllStudents(String courseId){
        return userRegistersCourseRepository.getAllStudents(courseId);
    }

    @Override
    public String getCourseId(String courseToken){
        return userRegistersCourseRepository.getCourseId(courseToken);
    }
}
