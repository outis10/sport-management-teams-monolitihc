package com.outis.stm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.outis.stm.IntegrationTest;
import com.outis.stm.domain.Organization;
import com.outis.stm.domain.Team;
import com.outis.stm.domain.TeamOrganization;
import com.outis.stm.domain.User;
import com.outis.stm.repository.TeamOrganizationRepository;
import com.outis.stm.service.TeamOrganizationService;
import com.outis.stm.service.dto.TeamOrganizationDTO;
import com.outis.stm.service.mapper.TeamOrganizationMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamOrganizationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamOrganizationResourceIT {

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/team-organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeamOrganizationRepository teamOrganizationRepository;

    @Mock
    private TeamOrganizationRepository teamOrganizationRepositoryMock;

    @Autowired
    private TeamOrganizationMapper teamOrganizationMapper;

    @Mock
    private TeamOrganizationService teamOrganizationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamOrganizationMockMvc;

    private TeamOrganization teamOrganization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamOrganization createEntity(EntityManager em) {
        TeamOrganization teamOrganization = new TeamOrganization().active(DEFAULT_ACTIVE).createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamOrganization.setTeam(team);
        // Add required entity
        Organization organization;
        if (TestUtil.findAll(em, Organization.class).isEmpty()) {
            organization = OrganizationResourceIT.createEntity(em);
            em.persist(organization);
            em.flush();
        } else {
            organization = TestUtil.findAll(em, Organization.class).get(0);
        }
        teamOrganization.setOrganization(organization);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        teamOrganization.setCreatedBy(user);
        return teamOrganization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamOrganization createUpdatedEntity(EntityManager em) {
        TeamOrganization teamOrganization = new TeamOrganization().active(UPDATED_ACTIVE).createdAt(UPDATED_CREATED_AT);
        // Add required entity
        Team team;
        if (TestUtil.findAll(em, Team.class).isEmpty()) {
            team = TeamResourceIT.createUpdatedEntity(em);
            em.persist(team);
            em.flush();
        } else {
            team = TestUtil.findAll(em, Team.class).get(0);
        }
        teamOrganization.setTeam(team);
        // Add required entity
        Organization organization;
        if (TestUtil.findAll(em, Organization.class).isEmpty()) {
            organization = OrganizationResourceIT.createUpdatedEntity(em);
            em.persist(organization);
            em.flush();
        } else {
            organization = TestUtil.findAll(em, Organization.class).get(0);
        }
        teamOrganization.setOrganization(organization);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        teamOrganization.setCreatedBy(user);
        return teamOrganization;
    }

    @BeforeEach
    public void initTest() {
        teamOrganization = createEntity(em);
    }

    @Test
    @Transactional
    void createTeamOrganization() throws Exception {
        int databaseSizeBeforeCreate = teamOrganizationRepository.findAll().size();
        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);
        restTeamOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeCreate + 1);
        TeamOrganization testTeamOrganization = teamOrganizationList.get(teamOrganizationList.size() - 1);
        assertThat(testTeamOrganization.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTeamOrganization.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createTeamOrganizationWithExistingId() throws Exception {
        // Create the TeamOrganization with an existing ID
        teamOrganization.setId(1L);
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        int databaseSizeBeforeCreate = teamOrganizationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamOrganizationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeamOrganizations() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        // Get all the teamOrganizationList
        restTeamOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamOrganization.getId().intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamOrganizationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(teamOrganizationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamOrganizationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamOrganizationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamOrganizationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teamOrganizationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamOrganizationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamOrganizationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getTeamOrganization() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        // Get the teamOrganization
        restTeamOrganizationMockMvc
            .perform(get(ENTITY_API_URL_ID, teamOrganization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamOrganization.getId().intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTeamOrganization() throws Exception {
        // Get the teamOrganization
        restTeamOrganizationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTeamOrganization() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();

        // Update the teamOrganization
        TeamOrganization updatedTeamOrganization = teamOrganizationRepository.findById(teamOrganization.getId()).get();
        // Disconnect from session so that the updates on updatedTeamOrganization are not directly saved in db
        em.detach(updatedTeamOrganization);
        updatedTeamOrganization.active(UPDATED_ACTIVE).createdAt(UPDATED_CREATED_AT);
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(updatedTeamOrganization);

        restTeamOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamOrganizationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isOk());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
        TeamOrganization testTeamOrganization = teamOrganizationList.get(teamOrganizationList.size() - 1);
        assertThat(testTeamOrganization.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTeamOrganization.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamOrganizationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamOrganizationWithPatch() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();

        // Update the teamOrganization using partial update
        TeamOrganization partialUpdatedTeamOrganization = new TeamOrganization();
        partialUpdatedTeamOrganization.setId(teamOrganization.getId());

        partialUpdatedTeamOrganization.createdAt(UPDATED_CREATED_AT);

        restTeamOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamOrganization.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamOrganization))
            )
            .andExpect(status().isOk());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
        TeamOrganization testTeamOrganization = teamOrganizationList.get(teamOrganizationList.size() - 1);
        assertThat(testTeamOrganization.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTeamOrganization.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateTeamOrganizationWithPatch() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();

        // Update the teamOrganization using partial update
        TeamOrganization partialUpdatedTeamOrganization = new TeamOrganization();
        partialUpdatedTeamOrganization.setId(teamOrganization.getId());

        partialUpdatedTeamOrganization.active(UPDATED_ACTIVE).createdAt(UPDATED_CREATED_AT);

        restTeamOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamOrganization.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeamOrganization))
            )
            .andExpect(status().isOk());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
        TeamOrganization testTeamOrganization = teamOrganizationList.get(teamOrganizationList.size() - 1);
        assertThat(testTeamOrganization.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTeamOrganization.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamOrganizationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = teamOrganizationRepository.findAll().size();
        teamOrganization.setId(count.incrementAndGet());

        // Create the TeamOrganization
        TeamOrganizationDTO teamOrganizationDTO = teamOrganizationMapper.toDto(teamOrganization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teamOrganizationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamOrganization in the database
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamOrganization() throws Exception {
        // Initialize the database
        teamOrganizationRepository.saveAndFlush(teamOrganization);

        int databaseSizeBeforeDelete = teamOrganizationRepository.findAll().size();

        // Delete the teamOrganization
        restTeamOrganizationMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamOrganization.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TeamOrganization> teamOrganizationList = teamOrganizationRepository.findAll();
        assertThat(teamOrganizationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
