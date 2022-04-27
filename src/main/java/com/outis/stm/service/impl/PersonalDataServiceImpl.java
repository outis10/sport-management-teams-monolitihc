package com.outis.stm.service.impl;

import com.outis.stm.domain.PersonalData;
import com.outis.stm.repository.PersonalDataRepository;
import com.outis.stm.service.PersonalDataService;
import com.outis.stm.service.dto.PersonalDataDTO;
import com.outis.stm.service.mapper.PersonalDataMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PersonalData}.
 */
@Service
@Transactional
public class PersonalDataServiceImpl implements PersonalDataService {

    private final Logger log = LoggerFactory.getLogger(PersonalDataServiceImpl.class);

    private final PersonalDataRepository personalDataRepository;

    private final PersonalDataMapper personalDataMapper;

    public PersonalDataServiceImpl(PersonalDataRepository personalDataRepository, PersonalDataMapper personalDataMapper) {
        this.personalDataRepository = personalDataRepository;
        this.personalDataMapper = personalDataMapper;
    }

    @Override
    public PersonalDataDTO save(PersonalDataDTO personalDataDTO) {
        log.debug("Request to save PersonalData : {}", personalDataDTO);
        PersonalData personalData = personalDataMapper.toEntity(personalDataDTO);
        personalData = personalDataRepository.save(personalData);
        return personalDataMapper.toDto(personalData);
    }

    @Override
    public PersonalDataDTO update(PersonalDataDTO personalDataDTO) {
        log.debug("Request to save PersonalData : {}", personalDataDTO);
        PersonalData personalData = personalDataMapper.toEntity(personalDataDTO);
        personalData = personalDataRepository.save(personalData);
        return personalDataMapper.toDto(personalData);
    }

    @Override
    public Optional<PersonalDataDTO> partialUpdate(PersonalDataDTO personalDataDTO) {
        log.debug("Request to partially update PersonalData : {}", personalDataDTO);

        return personalDataRepository
            .findById(personalDataDTO.getId())
            .map(existingPersonalData -> {
                personalDataMapper.partialUpdate(existingPersonalData, personalDataDTO);

                return existingPersonalData;
            })
            .map(personalDataRepository::save)
            .map(personalDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonalDataDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PersonalData");
        return personalDataRepository.findAll(pageable).map(personalDataMapper::toDto);
    }

    public Page<PersonalDataDTO> findAllWithEagerRelationships(Pageable pageable) {
        return personalDataRepository.findAllWithEagerRelationships(pageable).map(personalDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonalDataDTO> findOne(Long id) {
        log.debug("Request to get PersonalData : {}", id);
        return personalDataRepository.findOneWithEagerRelationships(id).map(personalDataMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PersonalData : {}", id);
        personalDataRepository.deleteById(id);
    }
}
