package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Activity {
    private Integer activityId;
    private Integer sectionId;
    private Integer contentId;
    private Integer chapId;
    private  Integer tbookId;
}
