package com.outis.stm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.outis.stm.IntegrationTest;
import com.outis.stm.domain.PersonalData;
import com.outis.stm.domain.User;
import com.outis.stm.repository.PersonalDataRepository;
import com.outis.stm.service.PersonalDataService;
import com.outis.stm.service.dto.PersonalDataDTO;
import com.outis.stm.service.mapper.PersonalDataMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PersonalDataResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PersonalDataResourceIT {

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DAY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DAY = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/personal-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonalDataRepository personalDataRepository;

    @Mock
    private PersonalDataRepository personalDataRepositoryMock;

    @Autowired
    private PersonalDataMapper personalDataMapper;

    @Mock
    private PersonalDataService personalDataServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonalDataMockMvc;

    private PersonalData personalData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalData createEntity(EntityManager em) {
        PersonalData personalData = new PersonalData().phone(DEFAULT_PHONE).fullName(DEFAULT_FULL_NAME).birthDay(DEFAULT_BIRTH_DAY);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        personalData.setCreatedBy(user);
        return personalData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalData createUpdatedEntity(EntityManager em) {
        PersonalData personalData = new PersonalData().phone(UPDATED_PHONE).fullName(UPDATED_FULL_NAME).birthDay(UPDATED_BIRTH_DAY);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        personalData.setCreatedBy(user);
        return personalData;
    }

    @BeforeEach
    public void initTest() {
        personalData = createEntity(em);
    }

    @Test
    @Transactional
    void createPersonalData() throws Exception {
        int databaseSizeBeforeCreate = personalDataRepository.findAll().size();
        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);
        restPersonalDataMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeCreate + 1);
        PersonalData testPersonalData = personalDataList.get(personalDataList.size() - 1);
        assertThat(testPersonalData.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testPersonalData.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testPersonalData.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
    }

    @Test
    @Transactional
    void createPersonalDataWithExistingId() throws Exception {
        // Create the PersonalData with an existing ID
        personalData.setId(1L);
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        int databaseSizeBeforeCreate = personalDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonalDataMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = personalDataRepository.findAll().size();
        // set the field null
        personalData.setFullName(null);

        // Create the PersonalData, which fails.
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        restPersonalDataMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPersonalData() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        // Get all the personalDataList
        restPersonalDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personalData.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].birthDay").value(hasItem(DEFAULT_BIRTH_DAY.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPersonalDataWithEagerRelationshipsIsEnabled() throws Exception {
        when(personalDataServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPersonalDataMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(personalDataServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPersonalDataWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(personalDataServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPersonalDataMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(personalDataServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPersonalData() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        // Get the personalData
        restPersonalDataMockMvc
            .perform(get(ENTITY_API_URL_ID, personalData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(personalData.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.birthDay").value(DEFAULT_BIRTH_DAY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPersonalData() throws Exception {
        // Get the personalData
        restPersonalDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPersonalData() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();

        // Update the personalData
        PersonalData updatedPersonalData = personalDataRepository.findById(personalData.getId()).get();
        // Disconnect from session so that the updates on updatedPersonalData are not directly saved in db
        em.detach(updatedPersonalData);
        updatedPersonalData.phone(UPDATED_PHONE).fullName(UPDATED_FULL_NAME).birthDay(UPDATED_BIRTH_DAY);
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(updatedPersonalData);

        restPersonalDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalDataDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
        PersonalData testPersonalData = personalDataList.get(personalDataList.size() - 1);
        assertThat(testPersonalData.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testPersonalData.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testPersonalData.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void putNonExistingPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalDataDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePersonalDataWithPatch() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();

        // Update the personalData using partial update
        PersonalData partialUpdatedPersonalData = new PersonalData();
        partialUpdatedPersonalData.setId(personalData.getId());

        partialUpdatedPersonalData.fullName(UPDATED_FULL_NAME).birthDay(UPDATED_BIRTH_DAY);

        restPersonalDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalData.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonalData))
            )
            .andExpect(status().isOk());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
        PersonalData testPersonalData = personalDataList.get(personalDataList.size() - 1);
        assertThat(testPersonalData.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testPersonalData.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testPersonalData.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void fullUpdatePersonalDataWithPatch() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();

        // Update the personalData using partial update
        PersonalData partialUpdatedPersonalData = new PersonalData();
        partialUpdatedPersonalData.setId(personalData.getId());

        partialUpdatedPersonalData.phone(UPDATED_PHONE).fullName(UPDATED_FULL_NAME).birthDay(UPDATED_BIRTH_DAY);

        restPersonalDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalData.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonalData))
            )
            .andExpect(status().isOk());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
        PersonalData testPersonalData = personalDataList.get(personalDataList.size() - 1);
        assertThat(testPersonalData.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testPersonalData.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testPersonalData.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void patchNonExistingPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personalDataDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPersonalData() throws Exception {
        int databaseSizeBeforeUpdate = personalDataRepository.findAll().size();
        personalData.setId(count.incrementAndGet());

        // Create the PersonalData
        PersonalDataDTO personalDataDTO = personalDataMapper.toDto(personalData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalDataMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personalDataDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalData in the database
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePersonalData() throws Exception {
        // Initialize the database
        personalDataRepository.saveAndFlush(personalData);

        int databaseSizeBeforeDelete = personalDataRepository.findAll().size();

        // Delete the personalData
        restPersonalDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, personalData.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PersonalData> personalDataList = personalDataRepository.findAll();
        assertThat(personalDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
