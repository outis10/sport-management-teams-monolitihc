package com.outis.stm.web.rest;

import com.outis.stm.repository.TeamOrganizationRepository;
import com.outis.stm.service.TeamOrganizationService;
import com.outis.stm.service.dto.TeamOrganizationDTO;
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
 * REST controller for managing {@link com.outis.stm.domain.TeamOrganization}.
 */
@RestController
@RequestMapping("/api")
public class TeamOrganizationResource {

    private final Logger log = LoggerFactory.getLogger(TeamOrganizationResource.class);

    private static final String ENTITY_NAME = "teamOrganization";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamOrganizationService teamOrganizationService;

    private final TeamOrganizationRepository teamOrganizationRepository;

    public TeamOrganizationResource(
        TeamOrganizationService teamOrganizationService,
        TeamOrganizationRepository teamOrganizationRepository
    ) {
        this.teamOrganizationService = teamOrganizationService;
        this.teamOrganizationRepository = teamOrganizationRepository;
    }

    /**
     * {@code POST  /team-organizations} : Create a new teamOrganization.
     *
     * @param teamOrganizationDTO the teamOrganizationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamOrganizationDTO, or with status {@code 400 (Bad Request)} if the teamOrganization has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/team-organizations")
    public ResponseEntity<TeamOrganizationDTO> createTeamOrganization(@Valid @RequestBody TeamOrganizationDTO teamOrganizationDTO)
        throws URISyntaxException {
        log.debug("REST request to save TeamOrganization : {}", teamOrganizationDTO);
        if (teamOrganizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new teamOrganization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeamOrganizationDTO result = teamOrganizationService.save(teamOrganizationDTO);
        return ResponseEntity
            .created(new URI("/api/team-organizations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /team-organizations/:id} : Updates an existing teamOrganization.
     *
     * @param id the id of the teamOrganizationDTO to save.
     * @param teamOrganizationDTO the teamOrganizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamOrganizationDTO,
     * or with status {@code 400 (Bad Request)} if the teamOrganizationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamOrganizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/team-organizations/{id}")
    public ResponseEntity<TeamOrganizationDTO> updateTeamOrganization(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamOrganizationDTO teamOrganizationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TeamOrganization : {}, {}", id, teamOrganizationDTO);
        if (teamOrganizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamOrganizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamOrganizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TeamOrganizationDTO result = teamOrganizationService.update(teamOrganizationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamOrganizationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /team-organizations/:id} : Partial updates given fields of an existing teamOrganization, field will ignore if it is null
     *
     * @param id the id of the teamOrganizationDTO to save.
     * @param teamOrganizationDTO the teamOrganizationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamOrganizationDTO,
     * or with status {@code 400 (Bad Request)} if the teamOrganizationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teamOrganizationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamOrganizationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/team-organizations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeamOrganizationDTO> partialUpdateTeamOrganization(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamOrganizationDTO teamOrganizationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TeamOrganization partially : {}, {}", id, teamOrganizationDTO);
        if (teamOrganizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamOrganizationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teamOrganizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeamOrganizationDTO> result = teamOrganizationService.partialUpdate(teamOrganizationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teamOrganizationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /team-organizations} : get all the teamOrganizations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teamOrganizations in body.
     */
    @GetMapping("/team-organizations")
    public ResponseEntity<List<TeamOrganizationDTO>> getAllTeamOrganizations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of TeamOrganizations");
        Page<TeamOrganizationDTO> page;
        if (eagerload) {
            page = teamOrganizationService.findAllWithEagerRelationships(pageable);
        } else {
            page = teamOrganizationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /team-organizations/:id} : get the "id" teamOrganization.
     *
     * @param id the id of the teamOrganizationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamOrganizationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/team-organizations/{id}")
    public ResponseEntity<TeamOrganizationDTO> getTeamOrganization(@PathVariable Long id) {
        log.debug("REST request to get TeamOrganization : {}", id);
        Optional<TeamOrganizationDTO> teamOrganizationDTO = teamOrganizationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamOrganizationDTO);
    }

    /**
     * {@code DELETE  /team-organizations/:id} : delete the "id" teamOrganization.
     *
     * @param id the id of the teamOrganizationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/team-organizations/{id}")
    public ResponseEntity<Void> deleteTeamOrganization(@PathVariable Long id) {
        log.debug("REST request to delete TeamOrganization : {}", id);
        teamOrganizationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
