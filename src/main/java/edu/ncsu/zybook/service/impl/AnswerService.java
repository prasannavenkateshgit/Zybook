package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Answer;
import edu.ncsu.zybook.persistence.repository.IAnswerRepository;
import edu.ncsu.zybook.service.IAnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerService implements IAnswerService {

    private final IAnswerRepository answerRepository;

    public AnswerService(IAnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public Answer create(Answer answer) {
        Optional<Answer> result = answerRepository.findById(answer.getAnswerId(), answer.getQuestionId(), answer.getActivityId(), answer.getContentId(), answer.getSectionId(), answer.getChapId(), answer.getTbookId()
        );
        if (result.isEmpty()) {
            return answerRepository.create(answer);
        } else {
            throw new RuntimeException("Answer already exists");
        }
    }

    @Override
    public Optional<Answer> findById(int answerId, int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        return answerRepository.findById(answerId, questionId, activityId, contentId, sectionId, chapId, tbookId);
    }

    @Transactional
    @Override
    public Optional<Answer> update(Answer answer) {
        if (answerRepository.findById(answer.getAnswerId(), answer.getQuestionId(), answer.getActivityId(), answer.getContentId(), answer.getSectionId(), answer.getChapId(), answer.getTbookId()).isPresent()) {
            return answerRepository.update(answer);
        } else {
            throw new RuntimeException("Answer not found with the provided identifiers");
        }
    }

    @Transactional
    @Override
    public boolean delete(int questionId, int answerId, int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        Optional<Answer> existingAnswer = answerRepository.findById(questionId, answerId, activityId, contentId, sectionId, chapId, tbookId);
        if (existingAnswer.isPresent()) {
            return answerRepository.delete(questionId, answerId, activityId, contentId, sectionId, chapId, tbookId);
        } else {
            throw new RuntimeException("Answer not found with the provided identifiers");
        }
    }

    @Override
    public List<Answer> findAllByQuestion(int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        return answerRepository.findAllByQuestion(questionId,activityId, contentId, sectionId, chapId, tbookId);
    }
}

