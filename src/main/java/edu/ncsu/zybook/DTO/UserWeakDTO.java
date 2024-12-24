package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWeakDTO {
    String fname;
    String lname;
    String email;
    String courseId;
    String courseToken;
    String password;
}
