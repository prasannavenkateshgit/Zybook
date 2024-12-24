package edu.ncsu.zybook.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Content {

    protected boolean hidden;
    protected String ownedBy;
    protected Integer sectionId;
    protected Integer contentId;
    protected Integer tbookId;
    protected Integer chapId;
    protected String contentType;
}
