package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Activity;
import edu.ncsu.zybook.domain.model.UserParticipation;
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
public class UserParticipationRepository implements IUserParticipationRepository{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserParticipation> findById(int userId, int questionId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        String sql = "SELECT * FROM User_Participation WHERE user_id=? AND q_id = ? AND activity_id = ? AND content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        try{
            UserParticipation userParticipation = jdbcTemplate.queryForObject(sql,new Object[]{userId,questionId,activityId,contentId,sectionId,chapterId,tbookId},new UserParticipationRowMapper());
            return Optional.of(userParticipation);
        }
        catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<UserParticipation> findAllByActivity(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        String sql = "SELECT * FROM User_Participation WHERE user_id = ? AND activity_id = ? AND content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        return jdbcTemplate.query(sql,new Object[]{userId,activityId,contentId,sectionId,chapterId,tbookId},new UserParticipationRowMapper());
    }


    @Override
    @Transactional
    public Optional<UserParticipation> create(UserParticipation userParticipation) {
        String sql = "INSERT INTO User_Participation(user_id,activity_id,content_id,s_id,c_id,t_id,q_id,ans_id,score) VALUES(?,?,?,?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,userParticipation.getUserId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId(),userParticipation.getQuestionId(),userParticipation.getAnswerId(),userParticipation.getScore());
        if(rowsAffected > 0){
            return findById(userParticipation.getUserId(),userParticipation.getQuestionId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId());
        }
        else{
            throw new RuntimeException("UserParticipation could not be created");
        }
    }

    @Override
    public Optional<UserParticipation> update(UserParticipation userParticipation) {
        String sql = "UPDATE User_Participation SET score = ? WHERE user_id = ? AND q_id = ? AND activity_id = ? AND content_id = ? AND s_id = ? AND c_id = ? AND t_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,new Object[]{userParticipation.getScore(),userParticipation.getUserId(),userParticipation.getQuestionId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId(),userParticipation.getAnswerId()},new UserParticipationRowMapper());
        return rowsAffected > 0 ? Optional.of(userParticipation) : Optional.empty();
    }


    private static class UserParticipationRowMapper implements RowMapper<UserParticipation> {
        @Override
        public UserParticipation mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserParticipation userParticipation = new UserParticipation();
            userParticipation.setUserId(rs.getInt("user_id"));
            userParticipation.setQuestionId(rs.getInt("q_id"));
            userParticipation.setActivityId(rs.getInt("activity_id"));
            userParticipation.setContentId(rs.getInt("content_id"));
            userParticipation.setSectionId(rs.getInt("s_id"));
            userParticipation.setChapterId(rs.getInt("c_id"));
            userParticipation.setTbookId(rs.getInt("t_id"));
            userParticipation.setAnswerId(rs.getInt("ans_id"));
            userParticipation.setScore(rs.getInt("score"));
            return userParticipation;
        }
    }
}
