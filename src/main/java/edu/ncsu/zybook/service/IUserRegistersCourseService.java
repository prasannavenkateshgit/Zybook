package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.User;
import edu.ncsu.zybook.domain.model.UserRegistersCourse;

import java.util.List;
import java.util.Optional;

public interface IUserRegistersCourseService {
    UserRegistersCourse create(UserRegistersCourse userRegistersCourse);

    Optional<UserRegistersCourse> findById(int userId, String courseId);

    //Optional<Section> findByTitle(int tbookId, int chapterId, String title );

    Optional<UserRegistersCourse> update(UserRegistersCourse userRegistersCourse);

    boolean delete(int userId, String courseId);

    List<UserRegistersCourse> findAllByUser(int userId);

    int getCurrentCount(String courseId);

    int getCapacity(String courseId);

    List<User> getAllStudents(String courseId);

    String getCourseId(String courseToken);
}
