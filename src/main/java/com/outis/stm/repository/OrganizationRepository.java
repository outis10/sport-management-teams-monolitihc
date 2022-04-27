package com.outis.stm.repository;

import com.outis.stm.domain.Organization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Organization entity.
 */
@Repository
public interface OrganizationRepository extends OrganizationRepositoryWithBagRelationships, JpaRepository<Organization, Long> {
    @Query("select organization from Organization organization where organization.createdBy.login = ?#{principal.preferredUsername}")
    List<Organization> findByCreatedByIsCurrentUser();

    default Optional<Organization> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Organization> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Organization> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct organization from Organization organization left join fetch organization.createdBy",
        countQuery = "select count(distinct organization) from Organization organization"
    )
    Page<Organization> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct organization from Organization organization left join fetch organization.createdBy")
    List<Organization> findAllWithToOneRelationships();

    @Query("select organization from Organization organization left join fetch organization.createdBy where organization.id =:id")
    Optional<Organization> findOneWithToOneRelationships(@Param("id") Long id);
}
