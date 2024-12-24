package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.ActiveCourse;
import edu.ncsu.zybook.domain.model.Course;
import edu.ncsu.zybook.persistence.repository.ICourseRepository;
import edu.ncsu.zybook.persistence.repository.IActiveCourseRepository;
import edu.ncsu.zybook.service.IActiveCourseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActiveCourseService implements IActiveCourseService {
    private final IActiveCourseRepository activeCourseRepository;

    public ActiveCourseService(IActiveCourseRepository activeCourseRepository) {
        this.activeCourseRepository = activeCourseRepository;
    }

    @Override
    public ActiveCourse create(ActiveCourse activeCourse) {
        Optional<ActiveCourse> result = activeCourseRepository.findById(activeCourse.getCourseId());
        if (result.isEmpty()) {
            return activeCourseRepository.create(activeCourse);
        }
        else{
            throw new RuntimeException("Course already exists!");
        }
    }

    @Override
    public Optional<ActiveCourse> update(ActiveCourse activeCourse) {
        if(activeCourseRepository.findById(activeCourse.getCourseId()).isPresent()){
            return activeCourseRepository.update(activeCourse);
        }
        else{
            throw new RuntimeException("Active Course does not exist with id:" + activeCourse.getCourseId());
        }
    }

    @Override
    public boolean delete(String id) {
        Optional<ActiveCourse> result = activeCourseRepository.findById(id);
        if (result.isPresent()) {
            return activeCourseRepository.delete(id);
        }
        else{
            throw new RuntimeException("Active Course does not exist with id:" + id);
        }
    }

    @Override
    public Optional<ActiveCourse> findById(String id) {
        Optional<ActiveCourse> result = activeCourseRepository.findById(id);
        if(result.isPresent()) {
            ActiveCourse activeCourse = result.get();
            //List<Section> sections = sectionRepository.findAllByChapter(tbookId, cno);
            return Optional.of(activeCourse);
        }
        return Optional.empty();
    }
}
