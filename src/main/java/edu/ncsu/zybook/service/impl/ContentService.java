package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.service.IContentService;
import edu.ncsu.zybook.domain.model.Content;
import edu.ncsu.zybook.persistence.repository.IContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContentService implements IContentService {

    private final IContentRepository contentRepository;

    public ContentService(IContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public Content create(Content content) {
        Optional<Content> existingContent = contentRepository.findById(
                content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId()
        );
        if (existingContent.isEmpty()) {
            return contentRepository.create(content);
        } else {
            throw new RuntimeException("Content already exists for given identifiers.");
        }
    }

    @Override
    public Optional<Content> findById(int contentId, int sectionId, int chapId, int tbook_id) {
        return contentRepository.findById(contentId, sectionId, chapId, tbook_id);
    }

    @Override
    public Optional<Content> update(Content content) {

//        System.out.println("Content Service Layer: " + content);
        Optional<Content> existingContent = contentRepository.findById(
                content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId()
        );
        if (existingContent.isPresent()) {
            return contentRepository.update(content);
        } else {
            throw new RuntimeException("Content not found for update.");
        }
    }

    @Override
    @Transactional
    public boolean delete(int contentId, int sectionId, int chapId, int tbook_id) {
        Optional<Content> existingContent = contentRepository.findById(contentId, sectionId, chapId, tbook_id);
        if (existingContent.isPresent()) {
            return contentRepository.delete(contentId, sectionId, chapId, tbook_id);
        } else {
            throw new RuntimeException("Content not found for deletion.");
        }
    }

    @Override
    public List<Content> getAllContentBySection(int sectionId, int chapId, int tbookId) {
        return contentRepository.findAllBySection(sectionId, chapId, tbookId);
    }

    @Override
    public int getNextContentId(int contentId, int sectionId, int chapId, int tbook_id) {
        int size = getAllContentBySection(sectionId, chapId, tbook_id).size();
        if(contentId >= size) return -1;
        return contentId + 1;
    }

    @Override
    public int getPreviousContentId(int contentId, int sectionId, int chapId, int tbook_id) {
        int size = getAllContentBySection(sectionId, chapId, tbook_id).size();
        if(contentId <= 1) return -1;
        return contentId - 1;
    }
}

