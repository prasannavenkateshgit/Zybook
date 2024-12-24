package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.UserRegistersCourseDTO;
import edu.ncsu.zybook.domain.model.UserRegistersCourse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegistersCourseReadDTOMapper {
    UserRegistersCourse toEntity(UserRegistersCourseDTO userRegistersCourseDTO);
    UserRegistersCourseDTO toDTO(UserRegistersCourse userRegistersCourse);
}
