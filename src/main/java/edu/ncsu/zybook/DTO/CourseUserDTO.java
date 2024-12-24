package edu.ncsu.zybook.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUserDTO {
    private String courseTitle;
    private String firstName;
    private String lastName;
    private String role;
}