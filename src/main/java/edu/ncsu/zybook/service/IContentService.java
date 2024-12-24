package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.Content;

import java.util.List;
import java.util.Optional;

public interface IContentService {
    Content create(Content content);
    Optional<Content> findById(int contentId, int sectionId, int chapId, int tbookId);
    Optional<Content> update(Content content);
    boolean delete(int contentId, int sectionId, int chapId, int tbook_id);
    List<Content> getAllContentBySection(int sectionId, int chapId, int tbookId);
    int getNextContentId(int contentId, int sectionId, int chapId, int tbook_id);
    int getPreviousContentId(int contentId, int sectionId, int chapId, int tbook_id);
}
