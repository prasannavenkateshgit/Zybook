package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ActivityDTO;
import edu.ncsu.zybook.DTO.QuestionDTO;
import edu.ncsu.zybook.domain.model.Activity;
import edu.ncsu.zybook.domain.model.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionDTOMapper {
    Question toEntity(QuestionDTO questionDTO);
    QuestionDTO toDTO(Question question);
}
