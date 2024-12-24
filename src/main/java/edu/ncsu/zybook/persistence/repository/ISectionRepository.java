package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Section;
import java.util.List;
import java.util.Optional;

public interface ISectionRepository {

    Section create(Section section);

    Optional<Section> findById(int tbookId, int chapterNo, int sno);

    Optional<Section> findByTitle(int tbookId, int chapterId, String title );

    Optional<Section> update(Section section);

    boolean delete(int tbookId, int chapterNo, int sno);

    List<Section> findAllByChapter(int tbookId, int chapterNo);

    Integer countOfSectionsInChapPerTbook(int tbookId, int chapId);
}
