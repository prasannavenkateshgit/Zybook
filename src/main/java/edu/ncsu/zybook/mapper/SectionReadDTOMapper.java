package edu.ncsu.zybook.mapper;

import org.mapstruct.Mapper;
import edu.ncsu.zybook.DTO.SectionReadDTO;
import edu.ncsu.zybook.domain.model.Section;

@Mapper(componentModel = "spring")
public interface SectionReadDTOMapper {
    Section toEntity(SectionReadDTO sectionReadDTO);
    SectionReadDTO toDTO(Section section);
}
