package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Optional;

public interface IActiveCourseRepository{
    ActiveCourse create(ActiveCourse activeCourse);
    Optional<ActiveCourse> update(ActiveCourse activeCourse);
    boolean delete(String id);
    Optional<ActiveCourse> findById(String id);
}

