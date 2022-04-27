package com.outis.stm.service.mapper;

import com.outis.stm.domain.Organization;
import com.outis.stm.domain.Team;
import com.outis.stm.domain.TeamOrganization;
import com.outis.stm.domain.User;
import com.outis.stm.service.dto.OrganizationDTO;
import com.outis.stm.service.dto.TeamDTO;
import com.outis.stm.service.dto.TeamOrganizationDTO;
import com.outis.stm.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeamOrganization} and its DTO {@link TeamOrganizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamOrganizationMapper extends EntityMapper<TeamOrganizationDTO, TeamOrganization> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamName")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationName")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userLogin")
    TeamOrganizationDTO toDto(TeamOrganization s);

    @Named("teamName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TeamDTO toDtoTeamName(Team team);

    @Named("organizationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    OrganizationDTO toDtoOrganizationName(Organization organization);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
