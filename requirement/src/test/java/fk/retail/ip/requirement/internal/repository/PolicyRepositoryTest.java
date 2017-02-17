package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class PolicyRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    PolicyRepository policyRepository;

    @Test
    public void testFetchByFsns() {
        Group group = TestHelper.getGroup("Socks");
        Policy policy1 = TestHelper.getPolicy("fsn1", group);
        Policy policy2 = TestHelper.getPolicy(null, group);
        policyRepository.persist(policy1);
        policyRepository.persist(policy2);
        List<Policy> policies = policyRepository.fetchByFsns(Lists.newArrayList("fsn1"));
        Assert.assertEquals(1, policies.size());
        Assert.assertEquals(policy1, policies.get(0));
        Assert.assertEquals(group, policies.get(0).getGroup());
    }

    @Test
    public void testFetchByGroup() {
        Group group = TestHelper.getGroup("Socks");
        Policy policy1 = TestHelper.getPolicy("fsn1", group);
        Policy policy2 = TestHelper.getPolicy(null, group);
        policyRepository.persist(policy1);
        policyRepository.persist(policy2);
        List<Policy> policies = policyRepository.fetchByGroup(Lists.newArrayList(group.getId()));
        Assert.assertEquals(1, policies.size());
        Assert.assertEquals(policy2, policies.get(0));
        Assert.assertEquals(group, policies.get(0).getGroup());
    }


}
