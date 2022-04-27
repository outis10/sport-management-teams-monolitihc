package com.outis.stm.repository;

import com.outis.stm.domain.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Team entity.
 */
@Repository
public interface TeamRepository extends TeamRepositoryWithBagRelationships, JpaRepository<Team, Long> {
    @Query("select team from Team team where team.createdBy.login = ?#{principal.preferredUsername}")
    List<Team> findByCreatedByIsCurrentUser();

    @Query("select team from Team team where team.updatedBy.login = ?#{principal.preferredUsername}")
    List<Team> findByUpdatedByIsCurrentUser();

    default Optional<Team> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Team> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Team> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct team from Team team left join fetch team.createdBy left join fetch team.updatedBy",
        countQuery = "select count(distinct team) from Team team"
    )
    Page<Team> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct team from Team team left join fetch team.createdBy left join fetch team.updatedBy")
    List<Team> findAllWithToOneRelationships();

    @Query("select team from Team team left join fetch team.createdBy left join fetch team.updatedBy where team.id =:id")
    Optional<Team> findOneWithToOneRelationships(@Param("id") Long id);
}
