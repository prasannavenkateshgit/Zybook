package edu.ncsu.zybook.DTO;

import edu.ncsu.zybook.domain.model.Section;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChapterReadDTO {
    Integer  tbookId;
    Integer cno;
    String chapterCode;
    String title;
    boolean hidden;
    List<SectionWeakDTO> sections;
}
