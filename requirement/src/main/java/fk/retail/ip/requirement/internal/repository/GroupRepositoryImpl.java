package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import fk.retail.ip.requirement.internal.entities.Group;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;


public class GroupRepositoryImpl extends SimpleJpaGenericRepository<Group, Long>
        implements GroupRepository {

    @Inject
    public GroupRepositoryImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }
}
