package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.Section;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface ISectionService {
    Section create(Section section);
    Optional<Section> findById(int tbookId, int chapterId, int sno);
    Optional<Section> update(Section section);
    boolean delete(int tbookId, int chapterId, int sno);
    List<Section> findAllByChapter(int tbookId, int chapterId);
}
