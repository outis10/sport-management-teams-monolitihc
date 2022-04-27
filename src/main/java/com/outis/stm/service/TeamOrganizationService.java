package com.outis.stm.service;

import com.outis.stm.service.dto.TeamOrganizationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.outis.stm.domain.TeamOrganization}.
 */
public interface TeamOrganizationService {
    /**
     * Save a teamOrganization.
     *
     * @param teamOrganizationDTO the entity to save.
     * @return the persisted entity.
     */
    TeamOrganizationDTO save(TeamOrganizationDTO teamOrganizationDTO);

    /**
     * Updates a teamOrganization.
     *
     * @param teamOrganizationDTO the entity to update.
     * @return the persisted entity.
     */
    TeamOrganizationDTO update(TeamOrganizationDTO teamOrganizationDTO);

    /**
     * Partially updates a teamOrganization.
     *
     * @param teamOrganizationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TeamOrganizationDTO> partialUpdate(TeamOrganizationDTO teamOrganizationDTO);

    /**
     * Get all the teamOrganizations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TeamOrganizationDTO> findAll(Pageable pageable);

    /**
     * Get all the teamOrganizations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TeamOrganizationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" teamOrganization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TeamOrganizationDTO> findOne(Long id);

    /**
     * Delete the "id" teamOrganization.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
