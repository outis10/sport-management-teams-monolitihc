package com.outis.stm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.outis.stm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamOrganizationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamOrganizationDTO.class);
        TeamOrganizationDTO teamOrganizationDTO1 = new TeamOrganizationDTO();
        teamOrganizationDTO1.setId(1L);
        TeamOrganizationDTO teamOrganizationDTO2 = new TeamOrganizationDTO();
        assertThat(teamOrganizationDTO1).isNotEqualTo(teamOrganizationDTO2);
        teamOrganizationDTO2.setId(teamOrganizationDTO1.getId());
        assertThat(teamOrganizationDTO1).isEqualTo(teamOrganizationDTO2);
        teamOrganizationDTO2.setId(2L);
        assertThat(teamOrganizationDTO1).isNotEqualTo(teamOrganizationDTO2);
        teamOrganizationDTO1.setId(null);
        assertThat(teamOrganizationDTO1).isNotEqualTo(teamOrganizationDTO2);
    }
}
