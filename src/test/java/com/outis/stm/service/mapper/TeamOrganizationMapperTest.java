package com.outis.stm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamOrganizationMapperTest {

    private TeamOrganizationMapper teamOrganizationMapper;

    @BeforeEach
    public void setUp() {
        teamOrganizationMapper = new TeamOrganizationMapperImpl();
    }
}
