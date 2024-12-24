package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Chapter;
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
public class ChapterRepository implements IChapterRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Chapter> findByTitle(String title, int tbookId) {
        String sql = "SELECT * FROM chapter WHERE title = ? AND tbook_id = ?";
        try{
            Chapter chapter = jdbcTemplate.queryForObject(sql, new Object[]{title, tbookId}, new ChapterRowMapper());
            return Optional.of(chapter);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Chapter create(Chapter chapter) {
        String sql = "INSERT INTO Chapter (cno, chapter_code, title, isHidden, tbook_id) VALUES(?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, chapter.getCno(), chapter.getChapterCode(), chapter.getTitle(), chapter.isHidden(), chapter.getTbookId());
        if(rowsAffected > 0)
        {
            return findById(chapter.getCno(), chapter.getTbookId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted chapter."));
        }
        else{
            throw new RuntimeException("Failed to insert chapter.");
        }
    }

    @Transactional
    @Override
    public Optional<Chapter> update(Chapter chapter) {
        String sql = "UPDATE Chapter SET title = ?, isHidden = ?, chapter_code = ? WHERE cno = ? AND tbook_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, chapter.getTitle(), chapter.isHidden(), chapter.getChapterCode(), chapter.getCno(), chapter.getTbookId());
        return rowsAffected > 0 ? Optional.of(chapter) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean delete(int tbookId, int cno) {
        String sql = "DELETE FROM Chapter WHERE cno = ? AND tbook_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, cno, tbookId);
        return rowsAffected>0;
    }

    @Transactional
    public Optional<Chapter> findById(int cno, int tbookId) {
        String sql = "SELECT * FROM Chapter WHERE cno = ? AND tbook_id = ?";
        try{
            Chapter chapter = jdbcTemplate.queryForObject(sql, new Object[]{cno, tbookId}, new ChapterRowMapper());
            return Optional.of(chapter);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Chapter> findAllByTextbook(int tbook_id) {
        String sql = "SELECT * FROM Chapter WHERE tbook_id = ? order by cno asc";
        return jdbcTemplate.query(sql, new Object[]{tbook_id}, new ChapterRowMapper());
    }

    private static class ChapterRowMapper implements RowMapper<Chapter> {
        @Override
        public Chapter mapRow(ResultSet rs, int rowNum) throws SQLException {
            Chapter chapter = new Chapter();
            chapter.setCno(rs.getInt("cno"));
            chapter.setTitle(rs.getString("title"));
            chapter.setTbookId(rs.getInt("tbook_id"));
            chapter.setChapterCode(rs.getString("chapter_code"));
            chapter.setHidden(rs.getBoolean("isHidden"));
            return chapter;
        }
    }

}
