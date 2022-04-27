package com.outis.stm.web.rest;

import com.outis.stm.repository.PersonalDataRepository;
import com.outis.stm.service.PersonalDataService;
import com.outis.stm.service.dto.PersonalDataDTO;
import com.outis.stm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.outis.stm.domain.PersonalData}.
 */
@RestController
@RequestMapping("/api")
public class PersonalDataResource {

    private final Logger log = LoggerFactory.getLogger(PersonalDataResource.class);

    private static final String ENTITY_NAME = "personalData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonalDataService personalDataService;

    private final PersonalDataRepository personalDataRepository;

    public PersonalDataResource(PersonalDataService personalDataService, PersonalDataRepository personalDataRepository) {
        this.personalDataService = personalDataService;
        this.personalDataRepository = personalDataRepository;
    }

    /**
     * {@code POST  /personal-data} : Create a new personalData.
     *
     * @param personalDataDTO the personalDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personalDataDTO, or with status {@code 400 (Bad Request)} if the personalData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/personal-data")
    public ResponseEntity<PersonalDataDTO> createPersonalData(@Valid @RequestBody PersonalDataDTO personalDataDTO)
        throws URISyntaxException {
        log.debug("REST request to save PersonalData : {}", personalDataDTO);
        if (personalDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new personalData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PersonalDataDTO result = personalDataService.save(personalDataDTO);
        return ResponseEntity
            .created(new URI("/api/personal-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /personal-data/:id} : Updates an existing personalData.
     *
     * @param id the id of the personalDataDTO to save.
     * @param personalDataDTO the personalDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalDataDTO,
     * or with status {@code 400 (Bad Request)} if the personalDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personalDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/personal-data/{id}")
    public ResponseEntity<PersonalDataDTO> updatePersonalData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PersonalDataDTO personalDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PersonalData : {}, {}", id, personalDataDTO);
        if (personalDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PersonalDataDTO result = personalDataService.update(personalDataDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalDataDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /personal-data/:id} : Partial updates given fields of an existing personalData, field will ignore if it is null
     *
     * @param id the id of the personalDataDTO to save.
     * @param personalDataDTO the personalDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalDataDTO,
     * or with status {@code 400 (Bad Request)} if the personalDataDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personalDataDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personalDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/personal-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonalDataDTO> partialUpdatePersonalData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PersonalDataDTO personalDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PersonalData partially : {}, {}", id, personalDataDTO);
        if (personalDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonalDataDTO> result = personalDataService.partialUpdate(personalDataDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalDataDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /personal-data} : get all the personalData.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personalData in body.
     */
    @GetMapping("/personal-data")
    public ResponseEntity<List<PersonalDataDTO>> getAllPersonalData(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of PersonalData");
        Page<PersonalDataDTO> page;
        if (eagerload) {
            page = personalDataService.findAllWithEagerRelationships(pageable);
        } else {
            page = personalDataService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /personal-data/:id} : get the "id" personalData.
     *
     * @param id the id of the personalDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personalDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/personal-data/{id}")
    public ResponseEntity<PersonalDataDTO> getPersonalData(@PathVariable Long id) {
        log.debug("REST request to get PersonalData : {}", id);
        Optional<PersonalDataDTO> personalDataDTO = personalDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(personalDataDTO);
    }

    /**
     * {@code DELETE  /personal-data/:id} : delete the "id" personalData.
     *
     * @param id the id of the personalDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/personal-data/{id}")
    public ResponseEntity<Void> deletePersonalData(@PathVariable Long id) {
        log.debug("REST request to delete PersonalData : {}", id);
        personalDataService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
