package edu.ncsu.zybook.web.controller;

import edu.ncsu.zybook.DTO.AnswerDTO;
import edu.ncsu.zybook.DTO.QuestionDTO;
import edu.ncsu.zybook.domain.model.Answer;
import edu.ncsu.zybook.domain.model.Question;
import edu.ncsu.zybook.mapper.AnswerDTOMapper;
import edu.ncsu.zybook.mapper.QuestionDTOMapper;
import edu.ncsu.zybook.service.IAnswerService;
import edu.ncsu.zybook.service.IQuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/question")
public class QuestionController {
    IQuestionService questionService;
    IAnswerService answerService;
    QuestionDTOMapper questionDTOMapper;
    AnswerDTOMapper answerDTOMapper;

    public QuestionController(IQuestionService questionService, IAnswerService answerService,QuestionDTOMapper questionDTOMapper, AnswerDTOMapper answerDTOMapper) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.questionDTOMapper = questionDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @PostMapping()
    public String createQuestion(@ModelAttribute QuestionDTO questionDTO) {
        System.out.println("Entered create question post!!");
        Question question = questionDTOMapper.toEntity(questionDTO);
        System.out.println("Create question testing:"+question);

        questionService.create(question);

        System.out.println("Answer DOOO " +  Arrays.toString(questionDTO.getAnswers()));
        AnswerDTO[] answerDTOs = questionDTO.getAnswers();
        Arrays.asList(answerDTOs).stream().map(answerDTOMapper::toEntity).forEach(answer -> {
            answer.setQuestionId(question.getQuestion_id());
            answer.setActivityId(question.getActivity_id());
            answer.setContentId(question.getContent_id());
            answer.setSectionId(question.getSection_id());
            answer.setChapId(question.getChapter_id());
            answer.setTbookId(question.getTbook_id());
            System.out.println("Answer testing:"+answer);
            answerService.create(answer);
        });
        return "redirect:/activity?chapId="+question.getChapter_id()+"&sectionId="+question.getSection_id()+"&contentId="+question.getContent_id()+"&activityId="+question.getActivity_id()+"&tbookId="+question.getTbook_id(); //redirected to activity: getAllActivities view
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @GetMapping("/new")
    public String showCreateQuestionForm(@RequestParam("tbookId") int tbookId,
                                         @RequestParam("chapId") int chapId,
                                        @RequestParam("sectionId") int sectionId,
                                        @RequestParam("contentId") int contentId,
                                        @RequestParam("activityId") int activityId,
                                        Model model) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setTbook_id(tbookId);
        questionDTO.setChapter_id(chapId);
        questionDTO.setSection_id(sectionId);
        questionDTO.setContent_id(contentId);
        questionDTO.setActivity_id(activityId);
        AnswerDTO[] answerDTOS = new  AnswerDTO[4];
        for(int i=0; i<4; i++){answerDTOS[i] = new AnswerDTO(); answerDTOS[i].setAnswerId(i+1);}
        questionDTO.setAnswers(answerDTOS);
        model.addAttribute("question", questionDTO);
        return "question/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @GetMapping("/edit")
    public String showEditQuestionForm(@RequestParam("tbookId") int tbookId,
                                         @RequestParam("chapId") int chapId,
                                         @RequestParam("sectionId") int sectionId,
                                         @RequestParam("contentId") int contentId,
                                         @RequestParam("activityId") int activityId,
                                         @RequestParam("questionId") int questionId,
                                         Model model) {
        Optional<Question> question = questionService.findById(questionId,activityId,contentId,sectionId,chapId,tbookId);
        List<Answer> answers = answerService.findAllByQuestion(questionId,activityId,contentId,sectionId,chapId,tbookId);

        AnswerDTO[] answerDTO = answers.stream().map(answerDTOMapper::toDTO).toArray(AnswerDTO[]::new);

        if(question.isPresent()) {
            System.out.println("Question Object to send to update view"+question.get());
            model.addAttribute("question", question.get());
            QuestionDTO questionDTO = questionDTOMapper.toDTO(question.get());
            questionDTO.setAnswers(answerDTO);
            System.out.println("Question DTO Object to send to update view"+questionDTO);
            model.addAttribute("question", questionDTO);
            return "question/edit";
        }
        return "question/create";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @PutMapping
    public String updateQuestion(@ModelAttribute QuestionDTO questionDTO,
    @RequestParam("questionId") int questionId,
    @RequestParam("activityId") int activityId,
    @RequestParam("contentId") int contentId,
    @RequestParam("sectionId") int sectionId,
    @RequestParam("chapId") int chapterId,
    @RequestParam("tbookId") int tbookId) {
        System.out.println("Entered update question!!");
        Question question = questionDTOMapper.toEntity(questionDTO);
        questionService.update(question);

        AnswerDTO[] answerDTOs = questionDTO.getAnswers();
        Arrays.asList(answerDTOs).stream().map(answerDTOMapper::toEntity).forEach(answer -> {
            answer.setQuestionId(question.getQuestion_id());
            answer.setActivityId(question.getActivity_id());
            answer.setContentId(question.getContent_id());
            answer.setSectionId(question.getSection_id());
            answer.setChapId(question.getChapter_id());
            answer.setTbookId(question.getTbook_id());
            answerService.update(answer);
        });
        return "redirect:/activity?chapId="+chapterId+"&sectionId="+sectionId+"&contentId="+contentId+"&activityId="+activityId+"&tbookId="+tbookId;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'TA')")
    @DeleteMapping
    public String deleteQuestion(@RequestParam("questionId") int questionId,
                                 @RequestParam("activityId") int activityId,
                                 @RequestParam("contentId") int contentId,
                                 @RequestParam("sectionId") int sectionId,
                                 @RequestParam("chapterId") int chapterId,
                                 @RequestParam("tbookId") int tbookId,
                                 @ModelAttribute QuestionDTO questionDTO) {
        System.out.println("Entered delete question!!");
        questionService.delete(questionId,activityId,contentId,sectionId,chapterId,tbookId);
//        List<Answer> answers = answerService.findAllByQuestion(questionId,activityId,contentId,sectionId,chapterId,tbookId);
//        for(Answer answer: answers){
//            answerService.delete(answer.getAnswerId(), answer.getQuestionId(),answer.getActivityId(),answer.getContentId(),answer.getSectionId(),answer.getChapId(),answer.getTbookId());
//        }
        return "redirect:/activity?chapId="+chapterId+"&sectionId="+sectionId+"&contentId="+contentId+"&activityId="+activityId+"&tbookId="+tbookId;
    }
}
