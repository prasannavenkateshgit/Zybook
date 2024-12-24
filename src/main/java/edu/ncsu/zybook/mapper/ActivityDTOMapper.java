package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ActivityDTO;
import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.domain.model.Activity;
import edu.ncsu.zybook.domain.model.Chapter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityDTOMapper {
    Activity toEntity(ActivityDTO activityDTO);
    ActivityDTO toDTO(Activity activity);
}
