package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.UserParticipation;

import java.util.List;
import java.util.Optional;

public interface IUserParticipationService {
    Optional<UserParticipation> findById(int userId, int questionId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    List<UserParticipation> findAllQuestionsByActivity(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    Optional<UserParticipation> create(UserParticipation userParticipation);
    boolean isCorrect(UserParticipation userParticipation);
    int calculateScore(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    Optional<UserParticipation> update(UserParticipation userParticipation);
    boolean hasAttended(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);

}
