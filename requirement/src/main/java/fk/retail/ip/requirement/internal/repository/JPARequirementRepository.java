package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class JPARequirementRepository extends SimpleJpaGenericRepository<Requirement, Long> implements RequirementRepository {


    @Inject
    public JPARequirementRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Requirement> findRequirementByIds(List<Long> requirementIds) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findRequirementByIds", Requirement.class);
        query.setParameter("ids", requirementIds);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    @Override
    public List<Requirement> findAllEnabledRequirements(String state) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findEnabledRequirementsByState", Requirement.class);
        query.setParameter("state", state);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

}
