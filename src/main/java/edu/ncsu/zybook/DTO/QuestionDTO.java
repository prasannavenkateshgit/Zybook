package edu.ncsu.zybook.DTO;

import edu.ncsu.zybook.domain.model.Answer;
import edu.ncsu.zybook.domain.model.Section;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionDTO {
    private AnswerDTO[] answers;
    private Integer activity_id;
    private Integer content_id;
    private Integer section_id;
    private Integer tbook_id;
    private Integer chapter_id;
    private Integer question_id;
    private Integer answer_id;
    private String question;
    private Integer isHidden;
    private Answer answer;
    private Integer userResponse;
    private String justification;
}
