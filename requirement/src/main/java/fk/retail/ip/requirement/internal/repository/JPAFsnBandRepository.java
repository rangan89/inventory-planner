package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class JPAFsnBandRepository extends SimpleJpaGenericRepository<FsnBand, Long> implements FsnBandRepository {


    @Inject
    public JPAFsnBandRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<FsnBand> fetchBandDataForFSNs(Set<String> fsns) {

        TypedQuery<FsnBand> query = getEntityManager().createNamedQuery("fetchBandDataForFSNs", FsnBand.class);
        query.setParameter("fsns", fsns);
        List<FsnBand> fsnBands = query.getResultList();
        return fsnBands;
    }


}
