package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JpaIwtRequestItemRepository extends SimpleJpaGenericRepository<IwtRequestItem, Long>
        implements IwtRequestItemRepository {

    @Inject
    public JpaIwtRequestItemRepository(
            Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<IwtRequestItem> fetchByFsns(List<String> fsns, List<String> statuses) {
        TypedQuery<IwtRequestItem> iwtRequestItemQuery =
                getEntityManager().createNamedQuery("IwtRequestItem.fetchByFsnsInStatuses", IwtRequestItem.class);
        iwtRequestItemQuery.setParameter("fsns", fsns);
        iwtRequestItemQuery.setParameter("statuses", statuses);
        return iwtRequestItemQuery.getResultList();
    }
}
