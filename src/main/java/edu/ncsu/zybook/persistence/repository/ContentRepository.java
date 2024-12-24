package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Content;
import edu.ncsu.zybook.domain.model.ImageContent;
import edu.ncsu.zybook.domain.model.TextContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ContentRepository implements IContentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Content create(Content content) {
        System.out.println("Inside content repo"+content.toString());
        String sql = "INSERT INTO Content (content_id, s_id, c_id, t_id, content_type, owned_by, is_hidden) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(),
                content.getTbookId(), content.getContentType(), content.getOwnedBy(), content.isHidden());

        if (rowsAffected > 0) {
            switch (content.getContentType().toLowerCase()) {
                case "text":
                    if (content instanceof TextContent) return createTextContent((TextContent) content);
                    else throw new IllegalArgumentException("Invalid content type for TextContent");
                case "image":
                    if (content instanceof ImageContent) return createImageContent((ImageContent) content);
                    else throw new IllegalArgumentException("Invalid content type for ImageContent");
                default:
                    throw new RuntimeException("Unknown content type: " + content.getContentType());
            }
        } else {
            throw new RuntimeException("Failed to insert content.");
        }
    }

    private TextContent createTextContent(TextContent content) {
        String sql = "INSERT INTO TextContent (content_id, s_id, c_id, t_id, data) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(),
                content.getTbookId(), content.getData());
        if (rowsAffected > 0) {
            return content;
        } else {
            throw new RuntimeException("Failed to insert Text content.");
        }
    }

    private ImageContent createImageContent(ImageContent content) {
        String sql = "INSERT INTO ImageContent (content_id, s_id, c_id, t_id, data) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(),
                content.getTbookId(), content.getData());
        if (rowsAffected > 0) {
            return content;
        } else {
            throw new RuntimeException("Failed to insert Image content.");
        }
    }

    @Transactional
    @Override
    public Optional<Content> update(Content content) {

        // Update the base Content table
        String sql = "UPDATE Content SET content_type = ?, owned_by = ?, is_hidden = ? WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, content.getContentType(), content.getOwnedBy(), content.isHidden(),
                content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());

        // Update specific content tables based on content type
        switch (content.getContentType().toLowerCase()) {
            case "text":
                if (content instanceof TextContent) updateTextContent((TextContent) content);
                else throw new IllegalArgumentException("Invalid content type for TextContent");
                break;
            case "image":
                if (content instanceof ImageContent) updateImageContent((ImageContent) content);
                else throw new IllegalArgumentException("Invalid content type for ImageContent");
                break;
            default:
                throw new RuntimeException("Unknown content type: " + content.getContentType());
        }

        return rowsAffected > 0 ? Optional.of(content) : Optional.empty();
    }

    // Helper method for TextContent update
    private void updateTextContent(TextContent content) {
        String sql = "UPDATE TextContent SET data = ? WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        jdbcTemplate.update(sql, content.getData(), content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());
    }

    // Helper method for ImageContent update
    private void updateImageContent(ImageContent content) {
        String sql = "UPDATE ImageContent SET data = ? WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        jdbcTemplate.update(sql, content.getData(), content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());
    }


    @Transactional
    @Override
    public boolean delete(int contentId, int sectionId, int chapId, int tbook_id) {
        Optional<Content> searchRes = findById(contentId, sectionId,chapId, tbook_id);
        if(searchRes.isPresent()) {
            Content content = searchRes.get();
            switch (content.getContentType().toLowerCase()) {
                case "text":
                    if (content instanceof TextContent) deleteTextContent((TextContent) content);
                    else throw new IllegalArgumentException("Invalid content type for TextContent");
                    break;
                case "image":
                    if (content instanceof ImageContent) deleteImageContent((ImageContent) content);
                    else throw new IllegalArgumentException("Invalid content type for ImageContent");
                    break;
                default:
                    throw new RuntimeException("Unknown content type: " + content.getContentType());
            }
            String sql = "DELETE FROM Content WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());
            return rowsAffected > 0;
        }
        return false;
    }

    private void deleteTextContent(TextContent content) {
        String sql = "DELETE FROM TextContent WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());
    }

    private void deleteImageContent(ImageContent content) {
        String sql = "DELETE FROM ImageContent WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        jdbcTemplate.update(sql, content.getContentId(), content.getSectionId(), content.getChapId(), content.getTbookId());
    }

    @Override
    public Optional<Content> findById(int contentId, int sectionId, int chapId, int tbook_id) {
        String sql = "SELECT * FROM Content WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        try {
            Content content = jdbcTemplate.queryForObject(sql, new Object[]{contentId, sectionId, chapId, tbook_id}, new ContentRowMapper());
            return Optional.of(content);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Content> findAllBySection(int sectionId, int chapId, int tbook_id) {
        String sql = "SELECT * FROM Content WHERE s_id = ? AND c_id = ? AND t_id = ?";
//        System.out.println(" " + sectionId + " " + chapId + " "+ tbook_id );
        List<Content> contents = jdbcTemplate.query(sql, new Object[]{sectionId, chapId, tbook_id}, new ContentRowMapper());
//        System.out.println(contents);
        return contents;
    }

    private class ContentRowMapper implements RowMapper<Content> {
        @Override
        public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
            String contentType = rs.getString("content_type");
            Content content;

            switch (contentType.toLowerCase()) {
                case "text":
                    content = new TextContent();
                    ((TextContent) content).setData(fetchTextData(rs.getInt("content_id"), rs.getInt("s_id"), rs.getInt("c_id"), rs.getInt("t_id")));
                    break;
                case "image":
                    content = new ImageContent();
                    ((ImageContent) content).setData(fetchImageData(rs.getInt("content_id"), rs.getInt("s_id"), rs.getInt("c_id"), rs.getInt("t_id")));
                    break;
                default:
                    content = new Content();  // For general "activity" content or others
                    break;
            }

            content.setContentId(rs.getInt("content_id"));
            content.setSectionId(rs.getInt("s_id"));
            content.setChapId(rs.getInt("c_id"));
            content.setTbookId(rs.getInt("t_id"));
            content.setContentType(contentType);
            content.setOwnedBy(rs.getString("owned_by"));
            content.setHidden(rs.getBoolean("is_hidden"));

            return content;
        }

    }


    // Helper method to fetch text data for TextContent
    private String fetchTextData(int contentId, int sectionId, int chapId, int tbook_id) {
        String sql = "SELECT data FROM TextContent WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{contentId, sectionId, chapId, tbook_id}, String.class);
    }

    // Helper method to fetch image data for ImageContent
    private byte[] fetchImageData(int contentId, int sectionId, int chapId, int tbookId) {
        String sql = "SELECT data FROM ImageContent WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        byte[] imageData = jdbcTemplate.queryForObject(sql, new Object[]{contentId, sectionId, chapId, tbookId}, byte[].class);

        return imageData;
    }


}





