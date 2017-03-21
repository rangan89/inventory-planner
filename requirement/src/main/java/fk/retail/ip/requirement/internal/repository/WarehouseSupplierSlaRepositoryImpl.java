package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.ProcPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.WarehouseSupplierSla;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class WarehouseSupplierSlaRepositoryImpl extends SimpleJpaGenericRepository<WarehouseSupplierSla, Long> implements WarehouseSupplierSlaRepository {

    @Inject
    public WarehouseSupplierSlaRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public Optional<Integer> getSla(String vertical, String warehouse, String supplier) {
        TypedQuery<Integer> query = getEntityManager().createNamedQuery("WarehouseSupplierSla.fetchSlaByVerticalWhSupplier", Integer.class);
        query.setParameter("vertical", vertical);
        query.setParameter("warehouse", warehouse);
        query.setParameter("supplier", supplier);
        List<Integer> sla =  query.getResultList();
        if (sla.isEmpty()) {
            return getSla(vertical, warehouse);
        }
        return Optional.of(sla.get(0).intValue());
    }

    @Override
    public Optional<Integer> getSla(String vertical, String warehouse) {
        TypedQuery<Double> query = getEntityManager().createNamedQuery("WarehouseSupplierSla.fetchAvgSlaByVerticalWh", Double.class);
        query.setParameter("vertical", vertical);
        query.setParameter("warehouse", warehouse);
        Double sla =  query.getSingleResult();
        if (sla != null) {
            return Optional.of(sla.intValue());
        }
        return getSla(vertical);
    }

    @Override
    public Optional<Integer> getSla(String vertical) {
        TypedQuery<Double> query = getEntityManager().createNamedQuery("WarehouseSupplierSla.fetchAvgSlaByVertical", Double.class);
        query.setParameter("vertical", vertical);
        Double sla =  query.getSingleResult();
        if (sla != null) {
            return Optional.of(sla.intValue());
        }
        return Optional.empty();
    }
}
