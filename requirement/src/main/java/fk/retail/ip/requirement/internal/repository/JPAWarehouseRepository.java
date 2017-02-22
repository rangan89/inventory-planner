package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Set;

/**
 * Created by yogeshwari.k on 21/02/17.
 */
public class JPAWarehouseRepository extends SimpleJpaGenericRepository<Warehouse, Long> implements WarehouseRepository {
    @Inject
    public JPAWarehouseRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Warehouse> fetchWarehouseNameByCode(Set<String> whCodes) {
        TypedQuery<Warehouse> query = getEntityManager().createNamedQuery("fetchWarehouseNameByCode", Warehouse.class);
        query.setParameter("whCodes",whCodes);
        List<Warehouse> warehouses = query.getResultList();
        return warehouses;
    }
}

