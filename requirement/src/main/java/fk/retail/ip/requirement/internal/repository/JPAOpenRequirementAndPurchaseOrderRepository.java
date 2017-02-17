package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JPAOpenRequirementAndPurchaseOrderRepository extends SimpleJpaGenericRepository<OpenRequirementAndPurchaseOrder, Long>
        implements OpenRequirementAndPurchaseOrderRepository {
    @Inject
    public JPAOpenRequirementAndPurchaseOrderRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<OpenRequirementAndPurchaseOrder> fetchByFsns(List<String> fsns) {
        TypedQuery<OpenRequirementAndPurchaseOrder> query = getEntityManager()
                .createNamedQuery("OpenRequirementAndPurchaseOrder.fetchByFsns", OpenRequirementAndPurchaseOrder.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }
}
