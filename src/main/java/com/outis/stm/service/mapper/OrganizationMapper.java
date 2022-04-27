package com.outis.stm.service.mapper;

import com.outis.stm.domain.Organization;
import com.outis.stm.domain.User;
import com.outis.stm.service.dto.OrganizationDTO;
import com.outis.stm.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Organization} and its DTO {@link OrganizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper extends EntityMapper<OrganizationDTO, Organization> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userLogin")
    @Mapping(target = "owners", source = "owners", qualifiedByName = "userLoginSet")
    OrganizationDTO toDto(Organization s);

    @Mapping(target = "removeOwner", ignore = true)
    Organization toEntity(OrganizationDTO organizationDTO);

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
