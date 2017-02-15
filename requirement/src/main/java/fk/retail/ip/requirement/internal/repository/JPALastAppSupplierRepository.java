package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Created by yogeshwari.k on 16/02/17.
 */
public class JPALastAppSupplierRepository extends SimpleJpaGenericRepository<LastAppSupplier, Long> implements LastAppSupplierRepository {

    @Inject
    public JPALastAppSupplierRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<LastAppSupplier> fetchLastAppSupplierForFsns(Set<String> fsns) {
        TypedQuery<LastAppSupplier> query = getEntityManager().createNamedQuery("fetchLastAppSupplierForFsns", LastAppSupplier.class);
        query.setParameter("fsns", fsns);
        List<LastAppSupplier> lastAppSuppliers = query.getResultList();
        return lastAppSuppliers;
    }
}
