package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAGroupFsnRepository extends SimpleJpaGenericRepository<GroupFsn, Long> implements GroupFsnRepository {

    @Inject
    public JPAGroupFsnRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<GroupFsn> findByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<GroupFsn> query = getEntityManager()
                .createNamedQuery("GroupFsn.fetchByFsns", GroupFsn.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }
}
