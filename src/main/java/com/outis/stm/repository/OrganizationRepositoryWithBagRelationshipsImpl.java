package com.outis.stm.repository;

import com.outis.stm.domain.Organization;
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
public class OrganizationRepositoryWithBagRelationshipsImpl implements OrganizationRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Organization> fetchBagRelationships(Optional<Organization> organization) {
        return organization.map(this::fetchOwners);
    }

    @Override
    public Page<Organization> fetchBagRelationships(Page<Organization> organizations) {
        return new PageImpl<>(
            fetchBagRelationships(organizations.getContent()),
            organizations.getPageable(),
            organizations.getTotalElements()
        );
    }

    @Override
    public List<Organization> fetchBagRelationships(List<Organization> organizations) {
        return Optional.of(organizations).map(this::fetchOwners).orElse(Collections.emptyList());
    }

    Organization fetchOwners(Organization result) {
        return entityManager
            .createQuery(
                "select organization from Organization organization left join fetch organization.owners where organization is :organization",
                Organization.class
            )
            .setParameter("organization", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Organization> fetchOwners(List<Organization> organizations) {
        return entityManager
            .createQuery(
                "select distinct organization from Organization organization left join fetch organization.owners where organization in :organizations",
                Organization.class
            )
            .setParameter("organizations", organizations)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
