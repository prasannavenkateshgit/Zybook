package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ChapterReadDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChapterReadDTOMapper {
    Chapter toEntity(ChapterReadDTO chapterDTO);
    ChapterReadDTO toDTO(Chapter chapter);
}
