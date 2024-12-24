package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import edu.ncsu.zybook.domain.model.Textbook;
import edu.ncsu.zybook.mapper.ChapterWeakMapper;
import edu.ncsu.zybook.mapper.TextbookReadDTOMapper;
import edu.ncsu.zybook.persistence.repository.IChapterRepository;
import edu.ncsu.zybook.persistence.repository.ITextbookRepository;
import edu.ncsu.zybook.service.ITextbookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TextbookService implements ITextbookService {

    private final ITextbookRepository textbookRepository;
    private final IChapterRepository chapterRepository;
    private final TextbookReadDTOMapper textbookReadDTOMapper;

    private final ChapterWeakMapper chapterWeakMapper;

    public TextbookService(ITextbookRepository textbookRepository, IChapterRepository chapterRepository, TextbookReadDTOMapper textbookReadDTOMapper, ChapterWeakMapper chapterWeakMapper) {
        this.textbookRepository = textbookRepository;
        this.chapterRepository = chapterRepository;
        this.textbookReadDTOMapper = textbookReadDTOMapper;
        this.chapterWeakMapper = chapterWeakMapper;
    }


    @Override
    public Textbook create(Textbook textbook) {
        Optional<Textbook> result= textbookRepository.findByTitle(textbook.getTitle());
        if(result.isEmpty()) {
            return textbookRepository.create(textbook);
        }
        else {
            throw new RuntimeException("Textbook already exists");
        }
    }

    @Override
    public Optional<Textbook> findById( int id) {
        Optional<Textbook> result = textbookRepository.findById(id);
        if(result.isPresent()) {
            Textbook tbook =  result.get();
            List<Chapter> chapters = chapterRepository.findAllByTextbook(id) ; // need to add this to DTO
            return  Optional.of(tbook);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Textbook> update(Textbook textbook) {
        System.out.println("DEBUG start");
        if(textbookRepository.findById(textbook.getUid()).isPresent()) {
            System.out.println("DEBUG end");
            return textbookRepository.update(textbook);
        }
        else
            throw new RuntimeException("There is no textbook with  id: "+textbook.getUid());
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        Optional<Textbook> existingTextbook = textbookRepository.findById(id);
        if (existingTextbook.isPresent()) {
            textbookRepository.delete(id);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public List<Textbook> getAllTextbooks(int offset, int limit, String sortBy, String sortDirection) {
        return textbookRepository.findAll(offset, limit, sortBy, sortDirection);
    }
}
