package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreDTO {
    private Integer userId;
    private Integer activityId;
    private Integer contentId;
    private Integer sectionId;
    private Integer chapterId;
    private Integer tbookId;
    private Integer score;
}
