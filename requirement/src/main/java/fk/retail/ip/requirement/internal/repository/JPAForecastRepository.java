package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Forecast;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;

public class JPAForecastRepository extends SimpleJpaGenericRepository<Forecast, Long> implements ForecastRepository {

    @Inject
    public JPAForecastRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Forecast> fetchByFsns(Set<String> fsns) {
        if (CollectionUtils.isEmpty(fsns)) {
            return Lists.newArrayList();
        }
        TypedQuery<Forecast> query = getEntityManager().createNamedQuery("Forecast.fetchByFsns", Forecast.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();
    }
}
