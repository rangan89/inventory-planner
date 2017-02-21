package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

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
    public List<Requirement> findAllCurrentRequirements(String state) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findCurrentRequirementsByState", Requirement.class);
        query.setParameter("state", state);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    @Override
    public List<Requirement> findEnabledRequirementsByStateFsn(String state, Set<String> fsns) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findEnabledRequirementsByStateFsn", Requirement.class);
        query.setParameter("state", state);
        query.setParameter("fsns", fsns);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

}
