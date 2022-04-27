package com.outis.stm.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.outis.stm.domain.TeamOrganization} entity.
 */
public class TeamOrganizationDTO implements Serializable {

    private Long id;

    private Boolean active;

    private Instant createdAt;

    private TeamDTO team;

    private OrganizationDTO organization;

    private UserDTO createdBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamOrganizationDTO)) {
            return false;
        }

        TeamOrganizationDTO teamOrganizationDTO = (TeamOrganizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamOrganizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamOrganizationDTO{" +
            "id=" + getId() +
            ", active='" + getActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", team=" + getTeam() +
            ", organization=" + getOrganization() +
            ", createdBy=" + getCreatedBy() +
            "}";
    }
}
