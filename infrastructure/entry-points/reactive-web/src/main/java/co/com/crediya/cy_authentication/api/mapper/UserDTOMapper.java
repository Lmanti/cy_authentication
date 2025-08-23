package co.com.crediya.cy_authentication.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.model.user.User;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toResponse(User user);
    List<UserDTO> toResponseList(List<User> users);
    User toModel(CreateUserDTO createUserDTO);
    User toModel(EditUserDTO editUserDTO);
}
