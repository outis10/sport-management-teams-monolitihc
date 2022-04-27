package com.outis.stm.service.mapper;

import com.outis.stm.domain.PersonalData;
import com.outis.stm.domain.User;
import com.outis.stm.service.dto.PersonalDataDTO;
import com.outis.stm.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PersonalData} and its DTO {@link PersonalDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonalDataMapper extends EntityMapper<PersonalDataDTO, PersonalData> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userLogin")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "userLogin")
    PersonalDataDTO toDto(PersonalData s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
