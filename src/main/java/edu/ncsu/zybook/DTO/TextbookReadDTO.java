package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TextbookReadDTO {
    Integer uid;
    String title;
   List<ChapterWeakDTO> chapters;
}
