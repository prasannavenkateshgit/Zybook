package edu.ncsu.zybook.persistence.repository;

import edu.ncsu.zybook.domain.model.Question;

import java.util.List;
import java.util.Optional;

public interface IQuestionRepository {
    Question create(Question question);

    Optional<Question> findById(int questionId, int activityId, int contentId, int sectionId, int chapterId, int tbookId);

    Optional<Question> update(Question question);

    boolean delete(int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId);

    List<Question> findAllByActivity(int activityId, int contentId, int sectionId, int chapterId, int tbookId);
}
