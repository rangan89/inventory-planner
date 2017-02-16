package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by nidhigupta.m on 15/02/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class FsnBandRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    FsnBandRepository fsnBandRepository;


    @Test
    public void fetchBandDataForFSNsTest() {
        FsnBand fsnBand = getFsnBand();
        fsnBandRepository.persist(fsnBand);
        List<FsnBand> fsnBandList = fsnBandRepository.fetchBandDataForFSNs(new HashSet<String>(Arrays.asList("fsn")));
        Assert.assertEquals(fsnBand, fsnBandList.get(0));
    }

    private FsnBand getFsnBand() {

        FsnBand fsnBand = new FsnBand("fsn",2, 3, "Last 30 Days");
        fsnBand.setCreatedAt(new Date());
        return fsnBand;
    }

}
