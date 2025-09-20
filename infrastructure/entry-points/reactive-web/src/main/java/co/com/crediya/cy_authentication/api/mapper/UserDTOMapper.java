package co.com.crediya.cy_authentication.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserBasicInfo;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.record.UserRecord;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toResponse(UserRecord user);
    
    UserBasicInfo toResponseBasic(UserRecord user);

    List<UserDTO> toResponseList(List<UserRecord> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idType", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    List<UserBasicInfo> toResponseBasicList(List<UserRecord> users);

    @Mapping(target = "id", ignore = true)
    User toModel(CreateUserDTO createUserDTO);
    
    @Mapping(target = "id", ignore = true)
    User toModel(EditUserDTO editUserDTO);
}
