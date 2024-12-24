package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String fname;
    private String lname;
    private String email;
    private String password;
    private String roleName;
    private Integer userId;
    private String confirmPassword;
    private String newPassword;
}
