package edu.ncsu.zybook.domain.model;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Question {
    private Integer activity_id;
    private Integer content_id;
    private Integer section_id;
    private Integer tbook_id;
    private Integer chapter_id;
    private Integer question_id;
    private Integer answer_id;
    private String question;
    private Integer isHidden;
}
