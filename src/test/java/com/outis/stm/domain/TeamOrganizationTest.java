package com.outis.stm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.outis.stm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TeamOrganizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TeamOrganization.class);
        TeamOrganization teamOrganization1 = new TeamOrganization();
        teamOrganization1.setId(1L);
        TeamOrganization teamOrganization2 = new TeamOrganization();
        teamOrganization2.setId(teamOrganization1.getId());
        assertThat(teamOrganization1).isEqualTo(teamOrganization2);
        teamOrganization2.setId(2L);
        assertThat(teamOrganization1).isNotEqualTo(teamOrganization2);
        teamOrganization1.setId(null);
        assertThat(teamOrganization1).isNotEqualTo(teamOrganization2);
    }
}
