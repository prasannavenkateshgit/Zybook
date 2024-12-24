package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Content;
import edu.ncsu.zybook.domain.model.Section;
import edu.ncsu.zybook.persistence.repository.IChapterRepository;
import edu.ncsu.zybook.persistence.repository.ISectionRepository;
import edu.ncsu.zybook.persistence.repository.IContentRepository;
import edu.ncsu.zybook.service.ISectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService implements ISectionService {

    private final IChapterRepository chapterRepository;
    private final ISectionRepository sectionRepository;
    private final IContentRepository contentRepository;

    public SectionService(IChapterRepository chapterRepository, ISectionRepository sectionRepository, IContentRepository contentRepository) {
        this.chapterRepository = chapterRepository;
        this.sectionRepository = sectionRepository;
        this.contentRepository = contentRepository;
    }

    @Override
    public Section create(Section section) {
        Optional<Section> result= sectionRepository.findByTitle(section.getTbookId(), section.getChapId(), section.getTitle());
        if(result.isEmpty()) {
            return sectionRepository.create(section);
        }
        else {
            throw new RuntimeException("Section already exists");
        }
    }

    @Override
    public Optional<Section> findById(int tbookId, int chapterId, int sno) {

        Optional<Section> result = sectionRepository.findById(tbookId,chapterId,sno);
        if(result.isPresent()) {
            Section section =  result.get();
            List<Content> contents = contentRepository.findAllBySection(sno,chapterId,tbookId); // need to add this to DTO
            return  Optional.of(section);
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public Optional<Section> update(Section section) {
        if(sectionRepository.findById(section.getTbookId(), section.getChapId(), section.getSno()).isPresent())
            return sectionRepository.update(section);
        else
            throw new RuntimeException("There is no section with  id: "+ section.getSno());
    }

    @Override
    @Transactional
    public boolean delete(int tbookId, int chapterId, int sno) {
        Optional<Section> existingSection = sectionRepository.findById(tbookId,chapterId,sno);
        if (existingSection.isPresent()) {
            sectionRepository.delete(tbookId,chapterId,sno);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public List<Section> findAllByChapter(int tbookId, int chapterId) {
        return sectionRepository.findAllByChapter(tbookId, chapterId);
    }
}
