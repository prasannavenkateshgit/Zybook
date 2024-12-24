package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Textbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TextbookRepository implements ITextbookRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Textbook create(Textbook textbook){
        String sql = "INSERT INTO E_Textbook (title) VALUES(?)";
        int rowsAffected = jdbcTemplate.update(sql, textbook.getTitle());
        if(rowsAffected > 0)
        {
            return findByTitle(textbook.getTitle())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted textbook."));
        }
        else{
            throw new RuntimeException("Failed to insert textbook.");
        }
    }

    @Override
    public Optional<Textbook> findById(int id) {
        String sql = "SELECT * FROM E_Textbook WHERE uid = ?";
        try {
            Textbook textbook = jdbcTemplate.queryForObject(sql, new Object[]{id}, new TextbookRowMapper());
            return Optional.of(textbook);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Optional<Textbook> update(Textbook textbook) {
        String sql = "UPDATE E_Textbook SET title = ? WHERE uid = ?";
        int rowsAffected = jdbcTemplate.update(sql, textbook.getTitle(), textbook.getUid());
        return rowsAffected > 0 ? Optional.of(textbook) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM E_Textbook WHERE uid = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    @Override
    public List<Textbook> findAll(int offset, int limit,String sortBy, String sortDirection) {
        String validSortDirection = sortDirection.equalsIgnoreCase("DESC") ? "DESC" : "ASC";
        String validSortBy = validateSortBy(sortBy);
        String sql = "SELECT * FROM E_Textbook ORDER BY " + validSortBy + " " + validSortDirection + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{limit,offset}, new TextbookRowMapper());
    }

    @Override
    public Optional<Textbook> findByTitle(String title) {
        String sql = "SELECT * FROM E_Textbook WHERE title = ?";
        try {
            Textbook textbook = jdbcTemplate.queryForObject(sql, new Object[]{title}, new TextbookRowMapper());
            return Optional.of(textbook);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class TextbookRowMapper implements RowMapper<Textbook> {
        @Override
        public Textbook mapRow(ResultSet rs, int rowNum) throws SQLException {
            Textbook tbook = new Textbook();
            tbook.setUid(rs.getInt("uid"));
            tbook.setTitle(rs.getString("title"));
            return tbook;
        }
    }

    private String validateSortBy(String sortBy) {
        // List of allowed columns to sort by in table
        List<String> allowedSortColumns = List.of("uid", "title");

        if (allowedSortColumns.contains(sortBy)) {
            return sortBy;
        } else {
            return "uid";
        }
    }
}
