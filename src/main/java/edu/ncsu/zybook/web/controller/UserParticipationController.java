package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.*;
import edu.ncsu.zybook.domain.model.*;
import edu.ncsu.zybook.mapper.ActivityDTOMapper;
import edu.ncsu.zybook.mapper.AnswerDTOMapper;
import edu.ncsu.zybook.mapper.ContentReadDTOMapper;
import edu.ncsu.zybook.mapper.QuestionDTOMapper;
import edu.ncsu.zybook.security.CustomUserDetails;
import edu.ncsu.zybook.service.IActivityService;
import edu.ncsu.zybook.service.IContentService;
import edu.ncsu.zybook.service.IQuestionService;
import edu.ncsu.zybook.service.IUserParticipationService;
import edu.ncsu.zybook.service.impl.AnswerService;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/participation")
public class UserParticipationController {
    private final AnswerService answerService;
    IUserParticipationService userParticipationService;
    IContentService contentService;
    IActivityService activityService;
    IQuestionService questionService;
    QuestionDTOMapper questionDTOMapper;
    AnswerDTOMapper answerDTOMapper;
    ActivityDTOMapper activityDTOMapper;
    ContentReadDTOMapper contentReadDTOMapper;

    public UserParticipationController(AnswerService answerService, IUserParticipationService userParticipationService, IContentService contentService, IActivityService activityService, IQuestionService questionService, QuestionDTOMapper questionDTOMapper, AnswerDTOMapper answerDTOMapper, ActivityDTOMapper activityDTOMapper, ContentReadDTOMapper contentReadDTOMapper) {
        this.answerService = answerService;
        this.userParticipationService = userParticipationService;
        this.contentService = contentService;
        this.activityService = activityService;
        this.questionService = questionService;
        this.questionDTOMapper = questionDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
        this.activityDTOMapper = activityDTOMapper;
        this.contentReadDTOMapper = contentReadDTOMapper;
    }

    @GetMapping("/edit")
    public String getAllContent(@RequestParam("contentId") int contentId,
                                @RequestParam("sectionId") int sectionId,
                                @RequestParam("chapId") int chapId,
                                @RequestParam("tbookId") int tbookId,
                                @RequestParam("userId") int userId,
                                Model model) {
        Optional<Content> content = contentService.findById(contentId, sectionId, chapId, tbookId);
        if (content.isPresent()) {
            Content tmp = content.get();
            ContentReadDTO contentReadDTO  = contentReadDTOMapper.toDTO(tmp);
            model.addAttribute("content", contentReadDTO);

            List<Activity> activities = activityService.findAllByContent(contentId, sectionId, chapId, tbookId);
            List<ActivityDTO> activityDTOS = new ArrayList<>();

            activities.forEach(activity -> {
                ActivityDTO activityDTO = activityDTOMapper.toDTO(activity);

                List<Question> questions = questionService.findAllByActivity(activity.getActivityId(), activity.getContentId(), activity.getSectionId(), activity.getChapId(), activity.getTbookId());
                System.out.println("Questions Debug: " + questions);
                System.out.println(" Debug:  ---------------------&&&&&&&&&&%%%%%%%%%%@@@@@@@@@@@@@@@@@");

                List<QuestionDTO> questionDTOS = new ArrayList<>();

                questions.forEach(question -> {
                    QuestionDTO questionDTO = questionDTOMapper.toDTO(question);

                    List<Answer> answers = answerService.findAllByQuestion(
                            questionDTO.getQuestion_id(),
                            questionDTO.getActivity_id(),
                            questionDTO.getContent_id(),
                            questionDTO.getSection_id(),
                            questionDTO.getChapter_id(),
                            questionDTO.getTbook_id()
                    );

                    // Convert List<Answer> to AnswerDTO[]
                    AnswerDTO[] answerDTOArray = new AnswerDTO[answers.size()];
                    for (int i = 0; i < answers.size(); i++) {
                        answerDTOArray[i] = answerDTOMapper.toDTO(answers.get(i));
                    }

                    questionDTO.setAnswers(answerDTOArray);
                    if (userParticipationService.hasAttended(userId, activity.getActivityId(), contentId, sectionId, chapId, tbookId)) {
                        Answer answer = answerService.findById(
                                question.getAnswer_id(),
                                question.getQuestion_id(),
                                question.getActivity_id(),
                                question.getContent_id(),
                                question.getSection_id(),
                                question.getChapter_id(),
                                question.getTbook_id()
                        ).orElseThrow(() -> new RuntimeException("Answer not found"));
                        UserParticipation userParticipation = userParticipationService.findById(userId,question.getQuestion_id(),question.getActivity_id(),question.getContent_id(),question.getSection_id(),question.getChapter_id(),question.getTbook_id()).orElseThrow(() -> new RuntimeException("User Participation not found"));
                        Integer userAns = userParticipation.getAnswerId();
                        String justification = answerService.findById(userAns,
                                question.getQuestion_id(),
                                question.getActivity_id(),
                                question.getContent_id(),
                                question.getSection_id(),
                                question.getChapter_id(),
                                question.getTbook_id()).get().getJustification();
                        questionDTO.setJustification(justification);
                        questionDTO.setUserResponse(userAns);
                        questionDTO.setAnswer(answer);
                    }
                    questionDTOS.add(questionDTO);
                });

                activityDTO.setQuestions(questionDTOS);
                if (userParticipationService.hasAttended(userId, activity.getActivityId(), contentId, sectionId, chapId, tbookId)) {
                    activityDTO.setScore(userParticipationService.calculateScore(userId, activity.getActivityId(), activity.getContentId(), sectionId, chapId, tbookId));
                }
                activityDTOS.add(activityDTO);


            });

            model.addAttribute("activities", activityDTOS);
            System.out.println(activityDTOS);
            List<UserParticipation> userParticipations = new ArrayList<>();

            model.addAttribute("userId", userId);

            int prev = contentService.getPreviousContentId (contentId, sectionId, chapId, tbookId);
            int next = contentService.getNextContentId (contentId, sectionId, chapId, tbookId);
            System.out.println("prev "+ prev+ "next" + next);
            model.addAttribute("prev", contentService.getPreviousContentId (contentId, sectionId, chapId, tbookId));
            model.addAttribute("next", contentService.getNextContentId (contentId, sectionId, chapId, tbookId));

            return "student/activity";
        }
        else {
            return "student/not-found";
//            throw  new RuntimeException("No such content");
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping
    public String submitParticipation(
            @RequestParam Map<String, String> formData,
            @RequestParam("contentId") int contentId,
            @RequestParam("sectionId") int sectionId,
            @RequestParam("chapId") int chapId,
            @RequestParam("tbookId") int tbookId,
            @RequestParam("userId") int userId,
            Model model) {

        List<UserParticipation> userParticipations = new ArrayList<>();

        // Loop through all entries in formData and process only 'responses' keys
        formData.forEach((key, value) -> {
            if (key.startsWith("responses")) {
                // Extract indices from the key pattern "responses[activityIndex][questionIndex]"
                String[] indices = key.replaceAll("[^0-9]+", " ").trim().split(" ");
                int activityIndex = Integer.parseInt(indices[0]);
                int questionIndex = Integer.parseInt(indices[1]);
                int answerId = Integer.parseInt(value);

                // Create UserParticipation object
                UserParticipation participation = new UserParticipation();
                participation.setUserId(userId);
                participation.setActivityId(activityIndex);
                participation.setContentId(contentId);
                participation.setSectionId(sectionId);
                participation.setChapterId(chapId);
                participation.setTbookId(tbookId);
                participation.setQuestionId(questionIndex);
                participation.setAnswerId(answerId);
                participation.setUserId(userId);
                userParticipationService.create(participation);
            }
        });

        return "redirect:/participation/edit?chapId="+chapId +"&sectionId="+sectionId+"&tbookId="+tbookId+"&userId="+userId+"&contentId="+contentId;
    }

    @GetMapping("/score")
    public String getScore(@AuthenticationPrincipal CustomUserDetails currentUser,
                           @RequestParam("tbookId") int tbookId,
                           @RequestParam("userId") int userId,
                           Model model
                           ){
        if( currentUser.getId() != userId) throw new RuntimeException("Incorrect access to different user's details");
        List<Activity> activities = activityService.findAllActivitiesByTextbook(tbookId);
        List<ScoreDTO> scores = new ArrayList<>();
        for(Activity activity: activities )
        {
            ScoreDTO score = new ScoreDTO();
            score.setUserId(userId);
            score.setTbookId(tbookId);
            score.setChapterId(activity.getChapId());
            score.setSectionId(activity.getSectionId());
            score.setContentId(activity.getContentId());
            score.setActivityId(activity.getActivityId());
            if (userParticipationService.hasAttended(userId, activity.getActivityId(), activity.getContentId(), activity.getSectionId(), activity.getChapId(), tbookId)) {
                score.setScore(userParticipationService.calculateScore(userId, activity.getActivityId(), activity.getContentId(), activity.getSectionId(), activity.getChapId(), tbookId));
            }
            else{
                score.setScore(-1);
            }
            scores.add(score);
        }
        System.out.println("Scores  ___>"+scores);
        model.addAttribute("scores", scores);
        return "student/participationScore";
    }

}