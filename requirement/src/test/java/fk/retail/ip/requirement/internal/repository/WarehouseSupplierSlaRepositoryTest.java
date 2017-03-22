package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.WarehouseSupplierSla;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Optional;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class WarehouseSupplierSlaRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    WarehouseSupplierSlaRepository warehouseSupplierSlaRepository;

    @Test
    public void testGetSla() {
        WarehouseSupplierSla sla1 = TestHelper.getWarehouseSupplierSla("vertical1", "wh1", "supp1", 5);
        warehouseSupplierSlaRepository.persist(sla1);
        WarehouseSupplierSla sla2 = TestHelper.getWarehouseSupplierSla("vertical1", "wh2", "supp1", 10);
        warehouseSupplierSlaRepository.persist(sla2);
        WarehouseSupplierSla sla3 = TestHelper.getWarehouseSupplierSla("vertical1", "wh1", "supp2", 30);
        warehouseSupplierSlaRepository.persist(sla3);
        warehouseSupplierSlaRepository.flushAndClear();

        //all present
        Optional<Integer> sla = warehouseSupplierSlaRepository.getSla("vertical1", "wh1", "supp1");
        Assert.assertEquals(5, (int)sla.get());
        //supplier not present
        sla = warehouseSupplierSlaRepository.getSla("vertical1", "wh1", "supp3");
        Assert.assertEquals(17, (int)sla.get());
        //supplier is null
        sla = warehouseSupplierSlaRepository.getSla("vertical1", "wh1", null);
        Assert.assertEquals(17, (int)sla.get());
        //supplier is empty
        sla = warehouseSupplierSlaRepository.getSla("vertical1", "wh1", "");
        Assert.assertEquals(17, (int)sla.get());
        //warehouse not present
        sla = warehouseSupplierSlaRepository.getSla("vertical1", "wh3", "supp1");
        Assert.assertEquals(15, (int)sla.get());
    }

}
