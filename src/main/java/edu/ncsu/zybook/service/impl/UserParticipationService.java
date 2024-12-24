package edu.ncsu.zybook.service.impl;

import edu.ncsu.zybook.domain.model.Question;
import edu.ncsu.zybook.domain.model.UserParticipation;
import edu.ncsu.zybook.persistence.repository.IQuestionRepository;
import edu.ncsu.zybook.persistence.repository.IUserParticipationRepository;
import edu.ncsu.zybook.service.IUserParticipationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserParticipationService implements IUserParticipationService {
    IUserParticipationRepository userParticipationRepository;
    IQuestionRepository questionRepository;

    public UserParticipationService(IUserParticipationRepository userParticipationRepository, IQuestionRepository questionRepository) {
        this.userParticipationRepository = userParticipationRepository;
        this.questionRepository = questionRepository;
    }
    @Override
    public Optional<UserParticipation> findById(int userId, int questionId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        Optional<UserParticipation> result = userParticipationRepository.findById(userId,questionId,activityId,contentId,sectionId,chapterId,tbookId);
        if (result.isPresent()) {
            return Optional.of(result.get());
        }
        return Optional.empty();
    }

    public boolean hasAttended(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        return findAllQuestionsByActivity(userId, activityId, contentId, sectionId, chapterId, tbookId)
                .stream()
                .anyMatch(e -> e.getScore() > 0);
    }

    @Override
    public List<UserParticipation> findAllQuestionsByActivity(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        List<UserParticipation> questionsUser = userParticipationRepository.findAllByActivity(userId, activityId, contentId, sectionId, chapterId, tbookId);
        return questionsUser;
    }

    @Override
    public Optional<UserParticipation> create(UserParticipation userParticipation) {
        Optional<UserParticipation> result = userParticipationRepository.findById(userParticipation.getUserId(),userParticipation.getQuestionId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId());
        if (result.isPresent()) {
            throw new RuntimeException("User already answered this question");
        }
        int score = isCorrect(userParticipation) ? 3 : 1;
        userParticipation.setScore(score);
        return userParticipationRepository.create(userParticipation);
    }

    @Override
    public boolean isCorrect(UserParticipation userParticipation) {
        Optional<Question> result = questionRepository.findById(userParticipation.getQuestionId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId());
        if (result.isPresent()) {
            Question question = result.get();
            System.out.println("User answer " + userParticipation.getAnswerId()+ " DB answer "+ question.getAnswer_id());
            System.out.println("Score cehck " + (userParticipation.getAnswerId() == question.getAnswer_id()));
            return userParticipation.getAnswerId() == question.getAnswer_id();
        }
        System.out.println("Score fail ");
        return false;
    }

    @Override
    public int calculateScore(int userId, int activityId, int contentId, int sectionId, int chapterId, int tbookId) {
        int score = 0;
        List<UserParticipation> questionUser = findAllQuestionsByActivity(userId, activityId, contentId, sectionId, chapterId, tbookId);
        for (UserParticipation questionByUser : questionUser) {
            if(isCorrect(questionByUser)) {
                score+=3;
            }
            else{
                score+=1;
            }
        }
        return score;
    }

    //TODO: Remove this function
    @Override
    public Optional<UserParticipation> update(UserParticipation userParticipation) {
        int calculateScore = calculateScore(userParticipation.getUserId(),userParticipation.getActivityId(),userParticipation.getContentId(),userParticipation.getSectionId(),userParticipation.getChapterId(),userParticipation.getTbookId());
        userParticipation.setScore(calculateScore);
        Optional<UserParticipation> result = userParticipationRepository.update(userParticipation);
        if(result.isPresent()) {
            return Optional.of(result.get());
        }
        return Optional.empty();
    }
}
