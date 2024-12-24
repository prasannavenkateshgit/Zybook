package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@NoArgsConstructor
public class Course {
    protected String courseId;
    protected String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected Date endDate;

    protected String courseType;
    protected Integer tbookId;
    protected int professorId;
}
