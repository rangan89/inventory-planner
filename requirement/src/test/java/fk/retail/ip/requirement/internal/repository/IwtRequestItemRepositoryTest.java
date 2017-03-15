package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.IwtRequest;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class IwtRequestItemRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    IwtRequestItemRepository iwtRequestItemRepository;

    @Test
    public void testFetchByFsns() {
        IwtRequest iwtRequest1 = TestHelper.getIwtRequest("abcd");
        IwtRequest iwtRequest2 = TestHelper.getIwtRequest("1234");
        IwtRequestItem iwtRequestItem1 = TestHelper.getIwtRequestItem("fsn1", "created", iwtRequest1);
        IwtRequestItem iwtRequestItem2 = TestHelper.getIwtRequestItem("fsn2", "dispatched", iwtRequest2);
        iwtRequestItemRepository.persist(iwtRequestItem1);
        iwtRequestItemRepository.persist(iwtRequestItem2);
        List<IwtRequestItem> iwtRequestItemsNotFound = iwtRequestItemRepository.fetchByFsns(Sets.newHashSet("fsn1"), Sets.newHashSet("dispatched"));
        Assert.assertEquals(0, iwtRequestItemsNotFound.size());
        List<IwtRequestItem> iwtRequestItems = iwtRequestItemRepository.fetchByFsns(Sets.newHashSet("fsn2"), Sets.newHashSet("dispatched"));
        Assert.assertEquals(1, iwtRequestItems.size());
        Assert.assertEquals(iwtRequestItem2, iwtRequestItems.get(0));
        Assert.assertEquals(iwtRequest2, iwtRequestItems.get(0).getIwtRequest());
    }
}
