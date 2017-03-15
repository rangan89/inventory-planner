package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
/**
 * Created by yogeshwari.k on 16/02/17.
 */
public class LastAppSupplierRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    LastAppSupplierRepository lastAppSupplierRepository;

    @Test
    public void fetchLastAppSupplierForFsnsTest() {
        LastAppSupplier lastAppSupplier = TestHelper.getLastAppSupplier("fsn", "wh","supplier", 150);
        lastAppSupplierRepository.persist(lastAppSupplier);
        List<LastAppSupplier> lastAppSupplierList = lastAppSupplierRepository.fetchLastAppSupplierForFsns(new HashSet<String>(Arrays.asList("fsn")));
        Assert.assertEquals(lastAppSupplier, lastAppSupplierList.get(0));
    }




}
