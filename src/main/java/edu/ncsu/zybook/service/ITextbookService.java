package edu.ncsu.zybook.service;

import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.domain.model.Textbook;

import java.util.List;
import java.util.Optional;

public interface ITextbookService {
    Textbook create(Textbook textbook);
    Optional<Textbook> findById(int id);
    Optional<Textbook> update(Textbook textbook);
    boolean delete(int id);
    List<Textbook> getAllTextbooks(int offset, int limit, String sortBy, String sortDirection);

}
