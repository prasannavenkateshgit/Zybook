package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserParticipation {
    private Integer userId;
    private Integer questionId;
    private Integer answerId;
    private Integer activityId;
    private Integer contentId;
    private Integer sectionId;
    private Integer chapterId;
    private Integer tbookId;
    private Integer score;
}
