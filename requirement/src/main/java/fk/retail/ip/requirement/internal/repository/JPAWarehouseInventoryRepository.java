package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.WarehouseInventory;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAWarehouseInventoryRepository extends SimpleJpaGenericRepository<WarehouseInventory, Long> implements WarehouseInventoryRepository {

    @Inject
    public JPAWarehouseInventoryRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<WarehouseInventory> fetchByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<WarehouseInventory> query =
                getEntityManager().createNamedQuery("WarehouseInventory.fetchByFsns", WarehouseInventory.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }
}
