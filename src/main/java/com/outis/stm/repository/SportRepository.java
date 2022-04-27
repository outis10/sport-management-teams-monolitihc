package com.outis.stm.repository;

import com.outis.stm.domain.Sport;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Sport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {}
