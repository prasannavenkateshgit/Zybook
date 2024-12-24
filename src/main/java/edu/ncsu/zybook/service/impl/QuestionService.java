package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Question;
import edu.ncsu.zybook.persistence.repository.QuestionRepository;
import edu.ncsu.zybook.service.IQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question create(Question question) {
        Optional<Question>result = questionRepository.findById(question.getQuestion_id(), question.getActivity_id(),question.getContent_id(),question.getSection_id(),question.getChapter_id(),question.getTbook_id());
        if(result.isEmpty()) {
            return questionRepository.create(question);
        }
        else{
            throw new RuntimeException("Question already exists!");
        }
    }

    @Override
    public Optional<Question> findById(int questionId,int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        Optional<Question> result= questionRepository.findById(questionId,activityId, contentId, sectionId, chapterId, tbookId);
        if(result.isPresent()) {
            return Optional.of(result.get());
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(int questionId, int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        Optional<Question> result= questionRepository.findById(questionId,activityId, contentId, sectionId, chapId, tbookId);
        if(result.isPresent()) {
            return questionRepository.delete(questionId,activityId, contentId, sectionId, chapId, tbookId);
        }
        return false;
    }

    @Override
    public Optional<Question> update(Question question) {
        Optional<Question> result= questionRepository.findById(question.getQuestion_id(),question.getActivity_id(), question.getContent_id(), question.getSection_id(), question.getChapter_id(), question.getTbook_id());
        if(result.isPresent()) {
            return questionRepository.update(question);
        }
        return Optional.empty();
    }

    @Override
    public List<Question> findAllByActivity(int activityId, int contentId, int sectionId, int chapId, int tbookId) {
        return questionRepository.findAllByActivity(activityId, contentId, sectionId, chapId, tbookId);
    }
}
