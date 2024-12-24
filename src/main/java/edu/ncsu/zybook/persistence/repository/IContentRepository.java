package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Content;

import java.util.List;
import java.util.Optional;

public interface IContentRepository {
    Content create(Content content);
    Optional<Content> update(Content content);
    boolean delete(int contentId, int sectionId, int chapId, int tbook_id);
    Optional<Content> findById(int contentId, int sectionId, int chapId, int tbook_id);
    List<Content> findAllBySection(int sectionId, int chapId, int tbook_id);
}
