package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

public class JPAPolicyRepository extends SimpleJpaGenericRepository<Policy, Long>
        implements PolicyRepository {

    @Inject
    public JPAPolicyRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Policy> fetch(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy> query =
                getEntityManager().createNamedQuery("Policy.fetchByFsns", Policy.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }

    @Override
    public List<Policy> fetch(Set<String> fsns, PolicyType policyType) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy>
                query =
                getEntityManager().createNamedQuery("Policy.fetchByFsnsAndType", Policy.class);
        query.setParameter("fsns", fsns);
        query.setParameter("type", policyType.toString());
        return query.getResultList();
    }

    @Override
    public List<Policy> fetchByGroup(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy> query =
                getEntityManager().createNamedQuery("Policy.fetchByGroupIds", Policy.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public List<Policy> fetchByGroup(Set<Long> ids, PolicyType policyType) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        TypedQuery<Policy> query = getEntityManager().createNamedQuery("Policy.fetchByGroupIdsAndType", Policy.class);
        query.setParameter("ids", ids);
        query.setParameter("type", policyType.toString());
        return query.getResultList();
    }
}
