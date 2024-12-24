package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Answer {
    private Integer questionId;
    private Integer answerId;
    private Integer activityId;
    private Integer contentId;
    private Integer sectionId;
    private Integer chapId;
    private Integer tbookId;
    private String answerText;
    private String justification;
}
