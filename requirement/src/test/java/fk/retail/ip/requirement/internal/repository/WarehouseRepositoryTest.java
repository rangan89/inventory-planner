package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by yogeshwari.k on 21/02/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class WarehouseRepositoryTest extends TransactionalJpaRepositoryTest {
    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    public void fetchWarehouseNameByCodeTest() {
        Set<String> whCodes = new HashSet<String>();
        IntStream.rangeClosed(1,20).forEach(i -> {
            warehouseRepository.persist(getWarehouse(i));
            whCodes.add("whCode"+String.valueOf(i));
        });
        List<Warehouse>
                warehouses = warehouseRepository.fetchWarehouseNameByCode(whCodes);
        Assert.assertEquals(20,warehouses.size());
    }

    private Warehouse getWarehouse(int i) {
        String whCode = "whCode" + String.valueOf(i);
        String whName = "whName" + String.valueOf(i);
        Warehouse warehouse = TestHelper.getWarehouse(whCode,whName);
        return  warehouse;
    }

}
