package com.outis.stm.service.impl;

import com.outis.stm.domain.TeamOrganization;
import com.outis.stm.repository.TeamOrganizationRepository;
import com.outis.stm.service.TeamOrganizationService;
import com.outis.stm.service.dto.TeamOrganizationDTO;
import com.outis.stm.service.mapper.TeamOrganizationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TeamOrganization}.
 */
@Service
@Transactional
public class TeamOrganizationServiceImpl implements TeamOrganizationService {

    private final Logger log = LoggerFactory.getLogger(TeamOrganizationServiceImpl.class);

    private final TeamOrganizationRepository teamOrganizationRepository;

    private final TeamOrganizationMapper teamOrganizationMapper;

    public TeamOrganizationServiceImpl(
        TeamOrganizationRepository teamOrganizationRepository,
        TeamOrganizationMapper teamOrganizationMapper
    ) {
        this.teamOrganizationRepository = teamOrganizationRepository;
        this.teamOrganizationMapper = teamOrganizationMapper;
    }

    @Override
    public TeamOrganizationDTO save(TeamOrganizationDTO teamOrganizationDTO) {
        log.debug("Request to save TeamOrganization : {}", teamOrganizationDTO);
        TeamOrganization teamOrganization = teamOrganizationMapper.toEntity(teamOrganizationDTO);
        teamOrganization = teamOrganizationRepository.save(teamOrganization);
        return teamOrganizationMapper.toDto(teamOrganization);
    }

    @Override
    public TeamOrganizationDTO update(TeamOrganizationDTO teamOrganizationDTO) {
        log.debug("Request to save TeamOrganization : {}", teamOrganizationDTO);
        TeamOrganization teamOrganization = teamOrganizationMapper.toEntity(teamOrganizationDTO);
        teamOrganization = teamOrganizationRepository.save(teamOrganization);
        return teamOrganizationMapper.toDto(teamOrganization);
    }

    @Override
    public Optional<TeamOrganizationDTO> partialUpdate(TeamOrganizationDTO teamOrganizationDTO) {
        log.debug("Request to partially update TeamOrganization : {}", teamOrganizationDTO);

        return teamOrganizationRepository
            .findById(teamOrganizationDTO.getId())
            .map(existingTeamOrganization -> {
                teamOrganizationMapper.partialUpdate(existingTeamOrganization, teamOrganizationDTO);

                return existingTeamOrganization;
            })
            .map(teamOrganizationRepository::save)
            .map(teamOrganizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamOrganizationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TeamOrganizations");
        return teamOrganizationRepository.findAll(pageable).map(teamOrganizationMapper::toDto);
    }

    public Page<TeamOrganizationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return teamOrganizationRepository.findAllWithEagerRelationships(pageable).map(teamOrganizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamOrganizationDTO> findOne(Long id) {
        log.debug("Request to get TeamOrganization : {}", id);
        return teamOrganizationRepository.findOneWithEagerRelationships(id).map(teamOrganizationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TeamOrganization : {}", id);
        teamOrganizationRepository.deleteById(id);
    }
}
