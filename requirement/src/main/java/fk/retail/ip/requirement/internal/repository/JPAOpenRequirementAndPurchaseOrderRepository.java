package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAOpenRequirementAndPurchaseOrderRepository extends SimpleJpaGenericRepository<OpenRequirementAndPurchaseOrder, Long>
        implements OpenRequirementAndPurchaseOrderRepository {
    @Inject
    public JPAOpenRequirementAndPurchaseOrderRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<OpenRequirementAndPurchaseOrder> fetchByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<OpenRequirementAndPurchaseOrder> query = getEntityManager()
                .createNamedQuery("OpenRequirementAndPurchaseOrder.fetchByFsns", OpenRequirementAndPurchaseOrder.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }
}
