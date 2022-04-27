package com.outis.stm.service.mapper;

import com.outis.stm.domain.Sport;
import com.outis.stm.service.dto.SportDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sport} and its DTO {@link SportDTO}.
 */
@Mapper(componentModel = "spring")
public interface SportMapper extends EntityMapper<SportDTO, Sport> {}
