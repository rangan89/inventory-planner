package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class OpenRequirementAndPurchaseOrderRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository;

    @Test
    public void testFetchByFsns() {
        OpenRequirementAndPurchaseOrder openRequirementAndPurchaseOrder = TestHelper.getOpenRequirementAndPurchaseOrder();
        openRequirementAndPurchaseOrderRepository.persist(openRequirementAndPurchaseOrder);
        List<OpenRequirementAndPurchaseOrder> openRequirementAndPurchaseOrders = openRequirementAndPurchaseOrderRepository.fetchByFsns(Sets.newHashSet("fsn1"));
        Assert.assertEquals(1, openRequirementAndPurchaseOrders.size());
        Assert.assertEquals(openRequirementAndPurchaseOrder, openRequirementAndPurchaseOrders.get(0));
    }
}
