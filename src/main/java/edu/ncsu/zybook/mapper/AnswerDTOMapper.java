package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.AnswerDTO;
import edu.ncsu.zybook.DTO.QuestionDTO;
import edu.ncsu.zybook.domain.model.Answer;
import edu.ncsu.zybook.domain.model.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerDTOMapper {
    Answer toEntity(AnswerDTO answerDTO);
    AnswerDTO toDTO(Answer answer);
}
