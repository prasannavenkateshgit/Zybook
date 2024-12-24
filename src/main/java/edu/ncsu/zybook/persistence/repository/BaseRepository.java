package edu.ncsu.zybook.persistence.repository;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    T create(T entity);
    Optional<T> update(T entity);
    boolean delete(int id);
    Optional<T> findById(int id);
    List<T> findAll(int offset, int limit, String sortBy, String sortDirection);
}
