package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.FsnBand;
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
public class FsnBandRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    FsnBandRepository fsnBandRepository;


    @Test
    public void fetchBandDataForFSNsTest() {
        FsnBand fsnBand = TestHelper.getFsnBand("fsn","Last 30 Days");
        fsnBandRepository.persist(fsnBand);
        List<FsnBand> fsnBandList = fsnBandRepository.fetchBandDataForFSNs(new HashSet<String>(Arrays.asList("fsn")));
        Assert.assertEquals(fsnBand, fsnBandList.get(0));
    }


}
