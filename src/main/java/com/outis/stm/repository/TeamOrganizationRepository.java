package com.outis.stm.repository;

import com.outis.stm.domain.TeamOrganization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TeamOrganization entity.
 */
@Repository
public interface TeamOrganizationRepository extends JpaRepository<TeamOrganization, Long> {
    @Query(
        "select teamOrganization from TeamOrganization teamOrganization where teamOrganization.createdBy.login = ?#{principal.preferredUsername}"
    )
    List<TeamOrganization> findByCreatedByIsCurrentUser();

    default Optional<TeamOrganization> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TeamOrganization> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TeamOrganization> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct teamOrganization from TeamOrganization teamOrganization left join fetch teamOrganization.team left join fetch teamOrganization.organization left join fetch teamOrganization.createdBy",
        countQuery = "select count(distinct teamOrganization) from TeamOrganization teamOrganization"
    )
    Page<TeamOrganization> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct teamOrganization from TeamOrganization teamOrganization left join fetch teamOrganization.team left join fetch teamOrganization.organization left join fetch teamOrganization.createdBy"
    )
    List<TeamOrganization> findAllWithToOneRelationships();

    @Query(
        "select teamOrganization from TeamOrganization teamOrganization left join fetch teamOrganization.team left join fetch teamOrganization.organization left join fetch teamOrganization.createdBy where teamOrganization.id =:id"
    )
    Optional<TeamOrganization> findOneWithToOneRelationships(@Param("id") Long id);
}
