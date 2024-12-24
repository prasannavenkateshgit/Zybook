package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SectionReadDTO {
    Integer sno;
    String title;
    boolean hidden;
    Integer chapId;
    Integer tbookId;
    List<ContentWeakDTO> contents;
}
