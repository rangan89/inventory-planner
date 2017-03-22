package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public interface ProductInfoRepository extends JpaGenericRepository<ProductInfo, Long> {

    List<ProductInfo> getProductInfo(Set<String> fsns);
}
