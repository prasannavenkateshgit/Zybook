package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.Question;

import java.util.List;
import java.util.Optional;

public interface IQuestionService {
    Question create(Question question);
    Optional<Question> findById(int questionId,int activityId, int contentId, int sectionId, int chapterId, int tbookId);
    boolean delete(int questionId,int activityId, int contentId, int sectionId, int chapId, int tbookId);
    Optional<Question> update(Question question);
    List<Question> findAllByActivity(int activityId, int contentId, int sectionId, int chapId, int tbookId);
}
