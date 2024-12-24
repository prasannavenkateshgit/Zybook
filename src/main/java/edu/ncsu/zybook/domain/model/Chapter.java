package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Chapter {
    private Integer cno;
    private String chapterCode;
    private String title;
    private boolean hidden;
    private Integer  tbookId;
}