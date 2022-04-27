package com.outis.stm.repository;

import com.outis.stm.domain.Team;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TeamRepositoryWithBagRelationshipsImpl implements TeamRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Team> fetchBagRelationships(Optional<Team> team) {
        return team.map(this::fetchOwners);
    }

    @Override
    public Page<Team> fetchBagRelationships(Page<Team> teams) {
        return new PageImpl<>(fetchBagRelationships(teams.getContent()), teams.getPageable(), teams.getTotalElements());
    }

    @Override
    public List<Team> fetchBagRelationships(List<Team> teams) {
        return Optional.of(teams).map(this::fetchOwners).orElse(Collections.emptyList());
    }

    Team fetchOwners(Team result) {
        return entityManager
            .createQuery("select team from Team team left join fetch team.owners where team is :team", Team.class)
            .setParameter("team", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Team> fetchOwners(List<Team> teams) {
        return entityManager
            .createQuery("select distinct team from Team team left join fetch team.owners where team in :teams", Team.class)
            .setParameter("teams", teams)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
