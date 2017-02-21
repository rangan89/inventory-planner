package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by agarwal.vaibhav on 18/02/17.
 */
public class JPAProductInfoRepository extends SimpleJpaGenericRepository<ProductInfo, Long> implements ProductInfoRepository {

    @Inject
    public JPAProductInfoRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<ProductInfo> getProductInfo(List<String> fsns) {
        TypedQuery<ProductInfo> query = getEntityManager().createNamedQuery("getProductInfo", ProductInfo.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();

    }
}
