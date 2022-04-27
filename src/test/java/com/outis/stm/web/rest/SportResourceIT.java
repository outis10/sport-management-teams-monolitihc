package com.outis.stm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.outis.stm.IntegrationTest;
import com.outis.stm.domain.Sport;
import com.outis.stm.repository.SportRepository;
import com.outis.stm.service.dto.SportDTO;
import com.outis.stm.service.mapper.SportMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SportResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_SLOTS_BY_GAME = 1;
    private static final Integer UPDATED_SLOTS_BY_GAME = 2;

    private static final String ENTITY_API_URL = "/api/sports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private SportMapper sportMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSportMockMvc;

    private Sport sport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sport createEntity(EntityManager em) {
        Sport sport = new Sport().name(DEFAULT_NAME).slotsByGame(DEFAULT_SLOTS_BY_GAME);
        return sport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sport createUpdatedEntity(EntityManager em) {
        Sport sport = new Sport().name(UPDATED_NAME).slotsByGame(UPDATED_SLOTS_BY_GAME);
        return sport;
    }

    @BeforeEach
    public void initTest() {
        sport = createEntity(em);
    }

    @Test
    @Transactional
    void createSport() throws Exception {
        int databaseSizeBeforeCreate = sportRepository.findAll().size();
        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);
        restSportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate + 1);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSport.getSlotsByGame()).isEqualTo(DEFAULT_SLOTS_BY_GAME);
    }

    @Test
    @Transactional
    void createSportWithExistingId() throws Exception {
        // Create the Sport with an existing ID
        sport.setId(1L);
        SportDTO sportDTO = sportMapper.toDto(sport);

        int databaseSizeBeforeCreate = sportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sportRepository.findAll().size();
        // set the field null
        sport.setName(null);

        // Create the Sport, which fails.
        SportDTO sportDTO = sportMapper.toDto(sport);

        restSportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlotsByGameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sportRepository.findAll().size();
        // set the field null
        sport.setSlotsByGame(null);

        // Create the Sport, which fails.
        SportDTO sportDTO = sportMapper.toDto(sport);

        restSportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSports() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList
        restSportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sport.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slotsByGame").value(hasItem(DEFAULT_SLOTS_BY_GAME)));
    }

    @Test
    @Transactional
    void getSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get the sport
        restSportMockMvc
            .perform(get(ENTITY_API_URL_ID, sport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sport.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slotsByGame").value(DEFAULT_SLOTS_BY_GAME));
    }

    @Test
    @Transactional
    void getNonExistingSport() throws Exception {
        // Get the sport
        restSportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport
        Sport updatedSport = sportRepository.findById(sport.getId()).get();
        // Disconnect from session so that the updates on updatedSport are not directly saved in db
        em.detach(updatedSport);
        updatedSport.name(UPDATED_NAME).slotsByGame(UPDATED_SLOTS_BY_GAME);
        SportDTO sportDTO = sportMapper.toDto(updatedSport);

        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sportDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSport.getSlotsByGame()).isEqualTo(UPDATED_SLOTS_BY_GAME);
    }

    @Test
    @Transactional
    void putNonExistingSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sportDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSportWithPatch() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport using partial update
        Sport partialUpdatedSport = new Sport();
        partialUpdatedSport.setId(sport.getId());

        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSport))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSport.getSlotsByGame()).isEqualTo(DEFAULT_SLOTS_BY_GAME);
    }

    @Test
    @Transactional
    void fullUpdateSportWithPatch() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport using partial update
        Sport partialUpdatedSport = new Sport();
        partialUpdatedSport.setId(sport.getId());

        partialUpdatedSport.name(UPDATED_NAME).slotsByGame(UPDATED_SLOTS_BY_GAME);

        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSport))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSport.getSlotsByGame()).isEqualTo(UPDATED_SLOTS_BY_GAME);
    }

    @Test
    @Transactional
    void patchNonExistingSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sportDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(count.incrementAndGet());

        // Create the Sport
        SportDTO sportDTO = sportMapper.toDto(sport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sportDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeDelete = sportRepository.findAll().size();

        // Delete the sport
        restSportMockMvc
            .perform(delete(ENTITY_API_URL_ID, sport.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
