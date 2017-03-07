package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Projection;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@Deprecated
//TODO: remove
public class ProjectionRepositoryImpl extends SimpleJpaGenericRepository<Projection, Long> implements ProjectionRepository {

    @Inject
    public ProjectionRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Projection> find(Set<String> fsns, boolean enabled) {
        TypedQuery<Projection> query = getEntityManager().createQuery("select p from Projection p where p.fsn in :fsns and p.enabled = :enabled", Projection.class);
        query.setParameter("fsns", fsns);
        query.setParameter("enabled", enabled?1:0);
        List<Projection> projections = query.getResultList();
        return projections;
    }
}

