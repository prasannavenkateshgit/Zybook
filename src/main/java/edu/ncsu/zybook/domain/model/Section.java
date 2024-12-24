package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Section {
    private Integer sno;
    private String title;
    private boolean hidden;
    private Integer chapId;
    private Integer tbookId;
}
