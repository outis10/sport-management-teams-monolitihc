package com.outis.stm.service.mapper;

import com.outis.stm.domain.Team;
import com.outis.stm.domain.User;
import com.outis.stm.service.dto.TeamDTO;
import com.outis.stm.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userLogin")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "userLogin")
    @Mapping(target = "owners", source = "owners", qualifiedByName = "userLoginSet")
    TeamDTO toDto(Team s);

    @Mapping(target = "removeOwner", ignore = true)
    Team toEntity(TeamDTO teamDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("userLoginSet")
    default Set<UserDTO> toDtoUserLoginSet(Set<User> user) {
        return user.stream().map(this::toDtoUserLogin).collect(Collectors.toSet());
    }
}
