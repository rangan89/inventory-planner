package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Projection;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import javax.inject.Provider;
import javax.persistence.EntityManager;


@Deprecated
//TODO: remove
public class ProjectionRepositoryImpl extends SimpleJpaGenericRepository<Projection, Long> implements ProjectionRepository {

    @Inject
    public ProjectionRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }
}
