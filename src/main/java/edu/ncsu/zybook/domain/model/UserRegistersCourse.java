package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
public class UserRegistersCourse {
    private Timestamp enrollmentDate;
    private String approvalStatus;
    private Integer userId;
    private String courseId;
    private String courseToken;
}
