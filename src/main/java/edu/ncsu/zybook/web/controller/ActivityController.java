package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.ActivityDTO;
import edu.ncsu.zybook.DTO.AnswerDTO;
import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.DTO.QuestionDTO;
import edu.ncsu.zybook.domain.model.*;
import edu.ncsu.zybook.mapper.ActivityDTOMapper;
import edu.ncsu.zybook.mapper.AnswerDTOMapper;
import edu.ncsu.zybook.mapper.QuestionDTOMapper;
import edu.ncsu.zybook.service.IActivityService;
import edu.ncsu.zybook.service.IAnswerService;
import edu.ncsu.zybook.service.IQuestionService;
import edu.ncsu.zybook.service.IUserService;
import edu.ncsu.zybook.service.impl.ActivityService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller()
@RequestMapping("/activity")
public class ActivityController {
    IActivityService iActivityService;
    IQuestionService iQuestionService;
    IAnswerService iAnswerService;

    ActivityDTOMapper activityDTOMapper;
    QuestionDTOMapper questionDTOMapper;
    AnswerDTOMapper answerDTOMapper;

    public ActivityController(IActivityService iActivityService, IQuestionService iQuestionService, IAnswerService iAnswerService,ActivityDTOMapper activityDTOMapper, QuestionDTOMapper questionDTOMapper, AnswerDTOMapper answerDTOMapper) {
        this.iActivityService = iActivityService;
        this.iQuestionService = iQuestionService;
        this.iAnswerService = iAnswerService;
        this.activityDTOMapper = activityDTOMapper;
        this.questionDTOMapper = questionDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
    }
    @GetMapping("/all")
    public String getAllActivities(@RequestParam("tbookId") int textbookId,
                                   @RequestParam("chapId") int chapterId,
                                   @RequestParam("sectionId") int sectionId,
                                   @RequestParam("contentId") int contentId,
                                   Model model) {
        List<Activity> allActivites= iActivityService.findAllByContent(contentId, sectionId, chapterId, textbookId);
        System.out.println("All Activities from Faculty"+allActivites.toString());
        model.addAttribute("allActivites", allActivites);
        model.addAttribute("contentId", contentId);
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("chapId", chapterId);
        model.addAttribute("tbookId", textbookId);
        return "activity/list";
    }
    @GetMapping
//    http://localhost:8080/activity/chapId=1&sectionId=1&contentId=1&activityId=1
    public String getActivity(@RequestParam("tbookId") int textbookId,
                                            @RequestParam("chapId") int chapterId,
                                            @RequestParam("sectionId") int sectionId,
                                            @RequestParam("contentId") int contentId,
                                            @RequestParam("activityId") int activityId,
                                            Model model) {
        System.out.println("DEeeeeBuG entered");
        Optional<Activity> activity = iActivityService.findById(activityId,contentId,sectionId,chapterId,textbookId);

        if(activity.isPresent()) {
//            System.out.println("DEeeeeBuG");
            List<Question> questions= iQuestionService.findAllByActivity(activityId,contentId,sectionId,chapterId, textbookId);
            ActivityDTO activityDTO = activityDTOMapper.toDTO(activity.get());
            List<QuestionDTO> questionDTOs = new ArrayList<>();
            for(Question question : questions) {
                List<Answer> answers = iAnswerService.findAllByQuestion(question.getQuestion_id(),question.getActivity_id(),question.getContent_id(),question.getSection_id(),question.getChapter_id(),question.getTbook_id());
                QuestionDTO questionDTO = questionDTOMapper.toDTO(question);
                AnswerDTO[] answerDTO = answers.stream().map(answerDTOMapper::toDTO).collect(Collectors.toList()).toArray(new AnswerDTO[0]);
                questionDTO.setAnswers(answerDTO);
                questionDTOs.add(questionDTO);
            }
            System.out.println("DEeeeeBuG exited:"+ Arrays.toString(questionDTOs.toArray()));
            activityDTO.setQuestions(questionDTOs);
            model.addAttribute("activity", activityDTO);

            return "activity/activity"; // return question template for each activity
        }
        //return activity not found
        return "activity/not-found";
    }

    @GetMapping("/query")
    public String activityQuery(@RequestParam("tbookId") int textbookId,
                                @RequestParam("chapId") int chapterId,
                                @RequestParam("sectionId") int sectionId,
                                @RequestParam("contentId") int contentId,
                                @RequestParam("activityId") int activityId,
                                Model model) {

        Optional<Activity> activityOptional = iActivityService.findById(activityId, contentId, sectionId, chapterId, textbookId);

        if (activityOptional.isPresent()) {
            Activity activity = activityOptional.get();
            ActivityDTO activityDTO = activityDTOMapper.toDTO(activity);

            Optional<Question> questionOptional = iQuestionService.findById(2, activityId, contentId, sectionId, chapterId, textbookId);
            if (questionOptional.isEmpty()) {
                return "activity/not-found";
            }

            Question question = questionOptional.get();
            QuestionDTO questionDTO = questionDTOMapper.toDTO(question);

            List<Answer> answers = iAnswerService.findAllByQuestion(2, activityId, contentId, sectionId, chapterId, textbookId)
                    .stream()
                    .filter(answer -> answer.getAnswerId() != question.getAnswer_id())
                    .toList();

            List<AnswerDTO> answerDTOs = answers.stream()
                    .map(answerDTOMapper::toDTO)
                    .toList();

            questionDTO.setAnswers(answerDTOs.toArray(new AnswerDTO[0]));

            List<QuestionDTO> questionDTOs = new ArrayList<>();
            questionDTOs.add(questionDTO);
            activityDTO.setQuestions(questionDTOs);

            model.addAttribute("activity", activityDTO);

            return "activity/activity";
        }

        return "activity/not-found";
    }


    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @PostMapping
    public  String createActivity(@ModelAttribute Activity activity) {
        System.out.println("Entered create activity post!!");
        iActivityService.create(activity);
        //return to view content page to list all activities
        return "redirect:/question/new?activityId=" + activity.getActivityId() + "&contentId="+activity.getContentId()+"&sectionId=" + activity.getSectionId() +
                "&chapId=" + activity.getChapId() +
                "&tbookId=" + activity.getTbookId();
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @DeleteMapping
    public  String deleteActivity(@RequestParam("tbookId") int textbookId,
                                                @RequestParam("chapId") int chapterId,
                                                @RequestParam("sectionId") int sectionId,
                                                @RequestParam("contentId") int contentId,
                                                @RequestParam("activityId") int activityId,
                                                Model model) {
        iActivityService.delete(activityId,contentId,sectionId,chapterId,textbookId);

        return "redirect:/activity/all?tbookId=" + textbookId +
                "&chapId=" + chapterId +
                "&sectionId=" + sectionId +
                "&contentId=" + contentId;
    }

    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN', 'TA')")
    @GetMapping("/new")
    public String showCreateForm(@RequestParam("tbookId") int textbookId,
                                 @RequestParam("chapId") int chapterId,
                                 @RequestParam("sectionId") int sectionId,
                                 @RequestParam("contentId") int contentId,
                                 Model model) {
        Activity activity = new Activity();
        activity.setTbookId(textbookId);
        activity.setChapId(chapterId);
        activity.setSectionId(sectionId);
        activity.setContentId(contentId);
        model.addAttribute("activity", activity);
        return "activity/create";
    }
}
