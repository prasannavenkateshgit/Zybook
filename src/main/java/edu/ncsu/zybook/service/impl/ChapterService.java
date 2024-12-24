package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Section;
import edu.ncsu.zybook.mapper.ChapterReadDTOMapper;
import edu.ncsu.zybook.mapper.SectionWeakMapper;
import edu.ncsu.zybook.persistence.repository.IChapterRepository;
import edu.ncsu.zybook.persistence.repository.ISectionRepository;
import edu.ncsu.zybook.service.IChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapterService implements IChapterService {

    private final IChapterRepository chapterRepository;
    private final ISectionRepository sectionRepository;
    private final SectionWeakMapper sectionWeakMapper;

    private final ChapterReadDTOMapper chapterReadDTOMapper;

    public ChapterService(IChapterRepository chapterRepository, ISectionRepository sectionRepository, SectionWeakMapper sectionWeakMapper, ChapterReadDTOMapper chapterReadDTOMapper) {
        this.chapterRepository = chapterRepository;
        this.sectionRepository = sectionRepository;
        this.sectionWeakMapper = sectionWeakMapper;
        this.chapterReadDTOMapper = chapterReadDTOMapper;
    }

    @Override
    public Chapter create(Chapter chapter) {
        Optional<Chapter> result = chapterRepository.findById(chapter.getCno(), chapter.getTbookId());
        if (result.isEmpty()) {
            return chapterRepository.create(chapter);
        }
        else{
            throw new RuntimeException("Chapter already exists!");
        }
    }

    @Override
    public Optional<Chapter> findById (int cno, int tbookId) {
        return chapterRepository.findById(cno, tbookId);
    }

    @Override
    @Transactional
    public Optional<Chapter> update(Chapter chapter) {
        if(chapterRepository.findById(chapter.getCno(), chapter.getTbookId()).isPresent()){
            return chapterRepository.update(chapter);
        }
        else{
            throw new RuntimeException("Chapter does not exist with id:" + chapter.getCno() + "in textbook: "+chapter.getTbookId());
        }
    }

    @Override
    @Transactional
    public boolean delete(int tbookId, int cno) {
        Optional<Chapter> result = chapterRepository.findById(cno, tbookId);
        if (result.isPresent()) {
            return chapterRepository.delete(tbookId, cno);
        }
        else{
            throw new RuntimeException("Chapter does not exist with id:" + cno + "in textbook: "+ tbookId);
        }
    }

    @Override
    public List<Chapter> findAllByTextbook(int tbookId) {
        return chapterRepository.findAllByTextbook(tbookId);
    }

    @Override
    public Optional<Chapter> findByTitle(String title, int tbookId) {
        Optional<Chapter> result = chapterRepository.findByTitle(title, tbookId);
        if(result.isPresent()) {
            Chapter chapter = result.get();
            return Optional.of(chapter);
        }
        else{
            throw new RuntimeException("Chapter does not exist with title:" + title +" in textbook:" + tbookId);
        }
    }
}
