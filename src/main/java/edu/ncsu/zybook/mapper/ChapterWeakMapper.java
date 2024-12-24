package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ChapterWeakDTO;
import edu.ncsu.zybook.domain.model.Chapter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChapterWeakMapper {
    ChapterWeakDTO toDTO(Chapter chapter);
}
