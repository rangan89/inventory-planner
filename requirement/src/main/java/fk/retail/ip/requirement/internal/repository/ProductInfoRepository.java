package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public interface ProductInfoRepository extends JpaGenericRepository<ProductInfo, Long> {

    List<ProductInfo> getProductInfo(List<String> fsns);

}
