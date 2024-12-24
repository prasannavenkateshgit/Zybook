package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Section;
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
public class SectionRepository implements ISectionRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Section create(Section section) {
        String sql = "INSERT INTO Section (sno, title, isHidden, tbook_id, chapter_no) VALUES(?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, section.getSno(), section.getTitle(), section.isHidden(), section.getTbookId(), section.getChapId());
        if (rowsAffected > 0) {
            return findById(section.getTbookId(), section.getChapId(), section.getSno())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted section."));
        } else {
            throw new RuntimeException("Failed to insert section.");
        }
    }

    @Override
    public Optional<Section> findById(int tbookId, int chapterId, int sno) {
        String sql = "SELECT * FROM Section WHERE tbook_id = ? AND chapter_no = ? AND sno = ?";
        try {
            Section section = jdbcTemplate.queryForObject(sql, new Object[]{tbookId, chapterId, sno}, new SectionRowMapper());
            return Optional.of(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public Optional<Section> findByTitle(int tbookId, int chapterId,String title ) {
        String sql = "SELECT * FROM Section WHERE tbook_id = ? AND chapter_no = ? AND title = ?";
        try {
            Section section = jdbcTemplate.queryForObject(sql, new Object[]{tbookId, chapterId, title}, new SectionRowMapper());
            return Optional.of(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Section> findAllByChapter(int tbookId, int chapterNo) {
        String sql = "SELECT * FROM Section WHERE tbook_id = ? AND chapter_no = ? ORDER BY sno";
        return jdbcTemplate.query(sql, new Object[]{tbookId, chapterNo}, new SectionRowMapper());
    }

    @Transactional
    @Override
    public Optional<Section> update(Section section) {
        String sql = "UPDATE Section SET title = ?, isHidden = ? WHERE tbook_id = ? AND chapter_no = ? AND sno = ?";
        int rowsAffected = jdbcTemplate.update(sql, section.getTitle(), section.isHidden(), section.getTbookId(), section.getChapId(), section.getSno());
        return rowsAffected > 0 ? Optional.of(section) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean delete(int tbookId, int chapterNo, int sno) {
        String sql = "DELETE FROM Section WHERE tbook_id = ? AND chapter_no = ? AND sno = ?";
        int rowsAffected = jdbcTemplate.update(sql, tbookId, chapterNo, sno);
        return rowsAffected > 0;
    }

    @Override
    public Integer countOfSectionsInChapPerTbook(int tbookId, int chapId) {
        String sql = "SELECT COUNT(*) FROM Section WHERE tbook_id = ? AND chapter_no = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{tbookId, chapId}, Integer.class);
    }

    private static class SectionRowMapper implements RowMapper<Section> {
        @Override
        public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
            Section section = new Section();
            section.setSno(rs.getInt("sno"));
            section.setTitle(rs.getString("title"));
            section.setHidden(rs.getBoolean("isHidden"));
            section.setTbookId(rs.getInt("tbook_id"));
            section.setChapId(rs.getInt("chapter_no"));
            return section;
        }
    }
}
