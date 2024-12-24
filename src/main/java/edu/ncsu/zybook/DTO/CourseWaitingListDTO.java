package edu.ncsu.zybook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseWaitingListDTO {
    private String courseId;
    private int waitingListCount;
}
