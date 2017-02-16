package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
public class JPAWeeklySaleRepository extends SimpleJpaGenericRepository<WeeklySale, Long> implements WeeklySaleRepository {

    @Inject
    public JPAWeeklySaleRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<WeeklySale> fetchWeeklySalesForFsns(Set<String> fsns) {
        TypedQuery<WeeklySale> query = getEntityManager().createNamedQuery("fetchWeeklySalesForFsns", WeeklySale.class);
        query.setParameter("fsns", fsns);
        List<WeeklySale> weeklySales = query.getResultList();
        return weeklySales;
    }



}
