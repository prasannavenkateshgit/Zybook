package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActiveCourse extends Course {

    private String courseToken;
    private Integer courseCapacity;
    private String courseID;
}
