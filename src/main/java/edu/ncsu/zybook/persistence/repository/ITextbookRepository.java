package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Textbook;
import org.w3c.dom.Text;

import java.util.Optional;

public interface ITextbookRepository extends BaseRepository<Textbook> {
    Optional<Textbook> findByTitle(String title);
}
