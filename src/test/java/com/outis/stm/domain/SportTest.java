package com.outis.stm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.outis.stm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sport.class);
        Sport sport1 = new Sport();
        sport1.setId(1L);
        Sport sport2 = new Sport();
        sport2.setId(sport1.getId());
        assertThat(sport1).isEqualTo(sport2);
        sport2.setId(2L);
        assertThat(sport1).isNotEqualTo(sport2);
        sport1.setId(null);
        assertThat(sport1).isNotEqualTo(sport2);
    }
}
