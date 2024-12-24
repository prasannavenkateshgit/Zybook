package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerDTO {
    private String answerText;
    private String justification;
    private Integer answerId;
}
