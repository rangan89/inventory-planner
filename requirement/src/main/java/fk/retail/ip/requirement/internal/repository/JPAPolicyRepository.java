package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAPolicyRepository extends SimpleJpaGenericRepository<Policy, Long> implements PolicyRepository {

    @Inject
    public JPAPolicyRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Policy> fetchByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy> query = getEntityManager().createNamedQuery("Policy.fetchByFsns", Policy.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }

    @Override
    public List<Policy> fetchByGroup(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy> query = getEntityManager().createNamedQuery("Policy.fetchByGroupIds", Policy.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
