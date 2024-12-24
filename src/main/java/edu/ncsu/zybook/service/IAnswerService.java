package edu.ncsu.zybook.service;

import edu.ncsu.zybook.domain.model.Answer;
import java.util.List;
import java.util.Optional;

public interface IAnswerService {
    Answer create(Answer answer);
    Optional<Answer> findById( int answerId, int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId);
    Optional<Answer> update(Answer answer);
    boolean delete(int questionId,int answerId, int activityId, int contentId, int sectionId, int chapId, int tbookId);
    List<Answer> findAllByQuestion(int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId);
}
