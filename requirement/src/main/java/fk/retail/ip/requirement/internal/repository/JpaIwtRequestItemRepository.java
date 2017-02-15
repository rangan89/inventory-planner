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
    public List<IwtRequestItem> fetchIwtIntransitItems(String fsn) {
        TypedQuery<IwtRequestItem> iwtRequestItemQuery =
                getEntityManager().createNamedQuery("fetchIwtRequestItemsInStatuses", IwtRequestItem.class);
        iwtRequestItemQuery.setParameter("fsn", fsn);
        iwtRequestItemQuery.setParameter("statuses", Lists.newArrayList("in-process", "dispatched", "requested"));
        return iwtRequestItemQuery.getResultList();
    }
}
