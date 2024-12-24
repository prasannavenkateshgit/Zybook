package edu.ncsu.zybook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCourseInfoDTO {
    private String courseId;
    private String facultyName;
    private int totalStudents;
}