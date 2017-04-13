package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Created by nidhigupta.m on 24/03/17.
 */
public class JPARequirementApprovalTransitionRepository extends SimpleJpaGenericRepository<RequirementApprovalTransition, Long>
        implements RequirementApprovalTransitionRepository {

    @Inject
    public JPARequirementApprovalTransitionRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }


    @Override
    public List<RequirementApprovalTransition> getApprovalTransition(String fromState, boolean forward) {
        TypedQuery<RequirementApprovalTransition> query = getEntityManager().
                createNamedQuery("RequirementApprovalTransition.getApprovalStateTransition",RequirementApprovalTransition.class);
        query.setParameter("fromState", fromState);
        query.setParameter("forward", forward);
        return query.getResultList();

    }
}
