package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.ProcPurchaseOrder;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class ProcPurchaseOrderRepositoryImpl extends SimpleJpaGenericRepository<ProcPurchaseOrder, Long> implements ProcPurchaseOrderRepository {

    @Inject
    public ProcPurchaseOrderRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public String find(String fsn) {
        TypedQuery<String> query = getEntityManager().createNamedQuery("ProcPurchaseOrder.fetchVerticalByFsn", String.class);
        query.setParameter("fsn", fsn);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
}
