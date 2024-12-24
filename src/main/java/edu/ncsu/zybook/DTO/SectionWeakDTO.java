package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionWeakDTO {
    Integer sno;
    String title;
    boolean hidden;
}
