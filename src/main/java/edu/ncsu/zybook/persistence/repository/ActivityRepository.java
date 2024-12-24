package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Activity;
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

import edu.ncsu.zybook.domain.model.Answer;

@Repository
public class ActivityRepository implements IActivityRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Activity create(Activity activity) {
        String sql = "INSERT INTO Activity (activity_id, content_id, s_id, c_id, t_id) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql,
                activity.getActivityId(),
                activity.getContentId(),
                activity.getSectionId(),
                activity.getChapId(),
                activity.getTbookId()
        );

        if (rowsAffected > 0) {
            return findById(activity.getActivityId(), activity.getContentId(), activity.getSectionId(), activity.getChapId(), activity.getTbookId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve newly inserted activity."));
        } else {
            throw new RuntimeException("Failed to insert activity.");
        }
    }

    @Override
    public Optional<Activity> findById(int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        String sql = "SELECT * FROM Activity WHERE activity_id = ? AND content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        try {
            Activity activity = jdbcTemplate.queryForObject(sql, new Object[]{activityId, contentId, sectionId, chapId, tbookId}, new ActivityRowMapper());
            return Optional.of(activity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public boolean delete(int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        String sql = "DELETE FROM Activity WHERE activity_id = ? AND content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, activityId, contentId, sectionId, chapId, tbookId);
        return rowsAffected > 0;
    }

    @Override
    public List<Activity> findAllByContent(int contentId, int sectionId, int chapId, int tbookId) {
        String sql = "SELECT * FROM Activity WHERE content_id = ? AND s_id = ? AND c_id = ? AND t_id = ? ORDER BY activity_id";
        System.out.println("Executing query with ActivityRowMapper...");
        List<Activity> allActivities = jdbcTemplate.query(sql, new Object[]{contentId, sectionId, chapId, tbookId}, new ActivityRowMapper());
        System.out.println("In activity respository:"+allActivities.size());
        return allActivities;
    }

    public List<Activity> findAllActivitiesByTextbook(int tbookId){
        String sql = "SELECT * FROM Activity WHERE t_id = ? ORDER BY activity_id";
        return jdbcTemplate.query(sql, new Object[]{tbookId}, new ActivityRowMapper());
    }

    private static class ActivityRowMapper implements RowMapper<Activity> {
        @Override
        public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
            Activity activity = new Activity();
            activity.setActivityId(rs.getInt("activity_id"));
            activity.setContentId(rs.getInt("content_id"));
            activity.setSectionId(rs.getInt("s_id"));
            activity.setChapId(rs.getInt("c_id"));
            activity.setTbookId(rs.getInt("t_id"));
            System.out.println("In activity repository row mapper:");
            return activity;
        }
    }
}