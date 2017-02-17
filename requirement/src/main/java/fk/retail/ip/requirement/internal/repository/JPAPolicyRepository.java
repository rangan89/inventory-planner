package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JPAPolicyRepository extends SimpleJpaGenericRepository<Policy, Long> implements PolicyRepository {

    @Inject
    public JPAPolicyRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Policy> fetchByFsns(List<String> fsns) {
        TypedQuery<Policy> query = getEntityManager().createNamedQuery("Policy.fetchByFsns", Policy.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }

    @Override
    public List<Policy> fetchByGroup(List<Long> ids) {
        TypedQuery<Policy> query = getEntityManager().createNamedQuery("Policy.fetchByGroupIds", Policy.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
