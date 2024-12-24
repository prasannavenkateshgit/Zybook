package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.UserWeakDTO;
import edu.ncsu.zybook.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWeakDTOMapper {
    UserWeakDTO toDTO(User user);
    User toEntity(UserWeakDTO userWeakDTO);
}
