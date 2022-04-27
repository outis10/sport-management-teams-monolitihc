package com.outis.stm.service;

import com.outis.stm.service.dto.PersonalDataDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.outis.stm.domain.PersonalData}.
 */
public interface PersonalDataService {
    /**
     * Save a personalData.
     *
     * @param personalDataDTO the entity to save.
     * @return the persisted entity.
     */
    PersonalDataDTO save(PersonalDataDTO personalDataDTO);

    /**
     * Updates a personalData.
     *
     * @param personalDataDTO the entity to update.
     * @return the persisted entity.
     */
    PersonalDataDTO update(PersonalDataDTO personalDataDTO);

    /**
     * Partially updates a personalData.
     *
     * @param personalDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PersonalDataDTO> partialUpdate(PersonalDataDTO personalDataDTO);

    /**
     * Get all the personalData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PersonalDataDTO> findAll(Pageable pageable);

    /**
     * Get all the personalData with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PersonalDataDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" personalData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PersonalDataDTO> findOne(Long id);

    /**
     * Delete the "id" personalData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
