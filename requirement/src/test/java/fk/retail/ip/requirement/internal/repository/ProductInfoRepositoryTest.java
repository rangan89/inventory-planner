package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by agarwal.vaibhav on 16/02/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class ProductInfoRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    ProductInfoRepository productInfoRepository;

//    @After
//    public void resetAutoIncrement() {
//        entityManagerProvider.get()
//                .createNativeQuery("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK")
//                .executeUpdate();
//    }

    @Test
    public void testGetProductInfo() {
        ProductInfo productInfo = getProductInfo(1);
        productInfoRepository.persist(productInfo);
        List<ProductInfo> productInfoList = productInfoRepository.getProductInfo(Lists.newArrayList(String.valueOf(1)));
        Assert.assertEquals(1, productInfoList.size());
        Assert.assertEquals(productInfo, productInfoList.get(0));
    }

    private ProductInfo getProductInfo(int i) {
       ProductInfo productInfo = new ProductInfo();
       productInfo.setFsn(String.valueOf(i));
       productInfo.setCategory("dummy_category");
       productInfo.setBrand("dummy_brand");
       productInfo.setFsp(1);
       productInfo.setTitle("dummy_title");
       productInfo.setSuperCategory("dummy_super_category");
       productInfo.setVertical("dummy_vertical");
       return  productInfo;
    }
}
