package com.outis.stm.repository;

import com.outis.stm.domain.PersonalData;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PersonalData entity.
 */
@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, Long> {
    @Query("select personalData from PersonalData personalData where personalData.createdBy.login = ?#{principal.preferredUsername}")
    List<PersonalData> findByCreatedByIsCurrentUser();

    @Query("select personalData from PersonalData personalData where personalData.updatedBy.login = ?#{principal.preferredUsername}")
    List<PersonalData> findByUpdatedByIsCurrentUser();

    default Optional<PersonalData> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PersonalData> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PersonalData> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct personalData from PersonalData personalData left join fetch personalData.createdBy left join fetch personalData.updatedBy",
        countQuery = "select count(distinct personalData) from PersonalData personalData"
    )
    Page<PersonalData> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct personalData from PersonalData personalData left join fetch personalData.createdBy left join fetch personalData.updatedBy"
    )
    List<PersonalData> findAllWithToOneRelationships();

    @Query(
        "select personalData from PersonalData personalData left join fetch personalData.createdBy left join fetch personalData.updatedBy where personalData.id =:id"
    )
    Optional<PersonalData> findOneWithToOneRelationships(@Param("id") Long id);
}
