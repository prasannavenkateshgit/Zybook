package edu.ncsu.zybook.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class ContentReadDTO {
    boolean hidden;
    String ownedBy;
    Integer sectionId;
    Integer contentId;
    Integer tbookId;
    Integer chapId;
    String contentType;
    String data;
    MultipartFile image;
}
