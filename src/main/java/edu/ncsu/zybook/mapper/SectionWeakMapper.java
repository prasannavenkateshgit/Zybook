package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.SectionWeakDTO;
import edu.ncsu.zybook.domain.model.Section;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SectionWeakMapper {
    SectionWeakDTO toDTO(Section section);
}
