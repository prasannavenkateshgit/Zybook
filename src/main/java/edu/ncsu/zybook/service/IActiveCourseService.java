package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.ActiveCourse;

import java.util.List;
import java.util.Optional;

public interface IActiveCourseService {
    ActiveCourse create(ActiveCourse activeCourse);
    Optional<ActiveCourse> update(ActiveCourse activeCourse);
    boolean delete(String id);
    Optional<ActiveCourse> findById(String id);
}
