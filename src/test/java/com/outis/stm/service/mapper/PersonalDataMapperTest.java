package com.outis.stm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalDataMapperTest {

    private PersonalDataMapper personalDataMapper;

    @BeforeEach
    public void setUp() {
        personalDataMapper = new PersonalDataMapperImpl();
    }
}
