package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.UserParticipation;

import java.util.List;
import java.util.Optional;

public interface IUserParticipationRepository {
    Optional<UserParticipation> findById(int userId, int questionId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    List<UserParticipation> findAllByActivity(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    Optional<UserParticipation> create(UserParticipation userParticipation);
    Optional<UserParticipation> update(UserParticipation userParticipation);
}
