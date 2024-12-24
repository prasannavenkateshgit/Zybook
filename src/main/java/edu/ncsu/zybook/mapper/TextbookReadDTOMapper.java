package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.TextbookReadDTO;
import edu.ncsu.zybook.domain.model.Textbook;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TextbookReadDTOMapper {
TextbookReadDTO toDTO(Textbook textbook);
Textbook toEntity(TextbookReadDTO textbookReadDTO);
}
