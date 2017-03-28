package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by agarwal.vaibhav on 18/02/17.
 */
public class JPAProductInfoRepository extends SimpleJpaGenericRepository<ProductInfo, Long> implements ProductInfoRepository {

    @Inject
    public JPAProductInfoRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<ProductInfo> getProductInfo(Set<String> fsns) {
        TypedQuery<ProductInfo> query = getEntityManager().createNamedQuery("getProductInfo", ProductInfo.class);
        query.setParameter("fsns", fsns);
        return query.getResultList();

    }

    @Override
    public List<String> getFsns(String vertical, String category, String superCategory, String businessUnit) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductInfo> criteriaQuery = criteriaBuilder.createQuery(ProductInfo.class);
        Root<ProductInfo> productInfoRoot = criteriaQuery.from(ProductInfo.class);
        CriteriaQuery<ProductInfo> select = criteriaQuery.select(productInfoRoot);
        List<Predicate> predicates = Lists.newArrayList();
        if (vertical != null) {
            Predicate predicate = criteriaBuilder.equal(productInfoRoot.get("vertical"),vertical);
            predicates.add(predicate);
        }
        if (category != null) {
            Predicate predicate = criteriaBuilder.equal(productInfoRoot.get("category"),category);
            predicates.add(predicate);
        }
        if (superCategory != null) {
            Predicate predicate = criteriaBuilder.equal(productInfoRoot.get("superCategory"),superCategory);
            predicates.add(predicate);
        }
        if (businessUnit != null) {
          //todo: add business unit info
        }
        select.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<ProductInfo> query = entityManager.createQuery(select);
        List<ProductInfo> productInfos = query.getResultList();
        return productInfos.stream().map(ProductInfo::getFsn).collect(Collectors.toList());
    }

}
