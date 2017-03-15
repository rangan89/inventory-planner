package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nidhigupta.m on 15/02/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class WeeklySaleRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    WeeklySaleRepository weeklySaleRepository;

    @Test
    public void fetchWeeklySalesForFsnsTest() {
        WeeklySale weeklySale = TestHelper.getWeeklySale("fsn", "wh1", 3, 90);
        weeklySaleRepository.persist(weeklySale);
        List<WeeklySale> weeklySaleList = weeklySaleRepository.fetchWeeklySalesForFsns(new HashSet<String>(Arrays.asList("fsn")));
        Assert.assertEquals(weeklySale, weeklySaleList.get(0));
    }

}
