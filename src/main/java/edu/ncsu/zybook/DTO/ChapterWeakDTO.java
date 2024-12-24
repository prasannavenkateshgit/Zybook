package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChapterWeakDTO {
    private Integer cno;
    private String chapterCode;
    private String title;
    private boolean hidden;
}
