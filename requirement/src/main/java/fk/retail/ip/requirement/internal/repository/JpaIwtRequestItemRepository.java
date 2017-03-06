package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JpaIwtRequestItemRepository extends SimpleJpaGenericRepository<IwtRequestItem, Long>
        implements IwtRequestItemRepository {

    @Inject
    public JpaIwtRequestItemRepository(
            Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<IwtRequestItem> fetchByFsns(Set<String> fsns, Set<String> statuses) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<IwtRequestItem> iwtRequestItemQuery =
                getEntityManager().createNamedQuery("IwtRequestItem.fetchByFsnsInStatuses", IwtRequestItem.class);
        iwtRequestItemQuery.setParameter("fsns", fsns);
        iwtRequestItemQuery.setParameter("statuses", statuses);
        return iwtRequestItemQuery.getResultList();
    }
}
