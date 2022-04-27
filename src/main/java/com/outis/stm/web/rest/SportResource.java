package com.outis.stm.web.rest;

import com.outis.stm.repository.SportRepository;
import com.outis.stm.service.SportService;
import com.outis.stm.service.dto.SportDTO;
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
 * REST controller for managing {@link com.outis.stm.domain.Sport}.
 */
@RestController
@RequestMapping("/api")
public class SportResource {

    private final Logger log = LoggerFactory.getLogger(SportResource.class);

    private static final String ENTITY_NAME = "sport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SportService sportService;

    private final SportRepository sportRepository;

    public SportResource(SportService sportService, SportRepository sportRepository) {
        this.sportService = sportService;
        this.sportRepository = sportRepository;
    }

    /**
     * {@code POST  /sports} : Create a new sport.
     *
     * @param sportDTO the sportDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sportDTO, or with status {@code 400 (Bad Request)} if the sport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sports")
    public ResponseEntity<SportDTO> createSport(@Valid @RequestBody SportDTO sportDTO) throws URISyntaxException {
        log.debug("REST request to save Sport : {}", sportDTO);
        if (sportDTO.getId() != null) {
            throw new BadRequestAlertException("A new sport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SportDTO result = sportService.save(sportDTO);
        return ResponseEntity
            .created(new URI("/api/sports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sports/:id} : Updates an existing sport.
     *
     * @param id the id of the sportDTO to save.
     * @param sportDTO the sportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sportDTO,
     * or with status {@code 400 (Bad Request)} if the sportDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sports/{id}")
    public ResponseEntity<SportDTO> updateSport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SportDTO sportDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Sport : {}, {}", id, sportDTO);
        if (sportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SportDTO result = sportService.update(sportDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sportDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sports/:id} : Partial updates given fields of an existing sport, field will ignore if it is null
     *
     * @param id the id of the sportDTO to save.
     * @param sportDTO the sportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sportDTO,
     * or with status {@code 400 (Bad Request)} if the sportDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sportDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sports/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SportDTO> partialUpdateSport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SportDTO sportDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sport partially : {}, {}", id, sportDTO);
        if (sportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SportDTO> result = sportService.partialUpdate(sportDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sportDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sports} : get all the sports.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sports in body.
     */
    @GetMapping("/sports")
    public ResponseEntity<List<SportDTO>> getAllSports(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Sports");
        Page<SportDTO> page = sportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sports/:id} : get the "id" sport.
     *
     * @param id the id of the sportDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sportDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sports/{id}")
    public ResponseEntity<SportDTO> getSport(@PathVariable Long id) {
        log.debug("REST request to get Sport : {}", id);
        Optional<SportDTO> sportDTO = sportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sportDTO);
    }

    /**
     * {@code DELETE  /sports/:id} : delete the "id" sport.
     *
     * @param id the id of the sportDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sports/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable Long id) {
        log.debug("REST request to delete Sport : {}", id);
        sportService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
