package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ContentWeakDTO;
import edu.ncsu.zybook.domain.model.Content;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentWeakMapper {
    ContentWeakDTO toDTO(Content content);
}
