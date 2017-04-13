package fk.retail.ip.requirement.internal.repository;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nidhigupta.m on 27/03/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class RequirementApprovalTransitionRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    RequirementApprovalTransitionRepository requirementApprovalTransitionRepository;

    @Test
    public void testGetApprovalTransition() {

        RequirementApprovalTransition requirementApprovalTransition = TestHelper.getRequirementApprovalTransition(1l, "proposed", "ipc_review", true);
        requirementApprovalTransitionRepository.persist(requirementApprovalTransition);
        List<RequirementApprovalTransition> requirementApprovalTransitions1 = requirementApprovalTransitionRepository.findAll();
        System.out.println("all size is " +requirementApprovalTransitions1 );
        List<RequirementApprovalTransition> requirementApprovalTransitions = requirementApprovalTransitionRepository.getApprovalTransition("proposed", true);
        Assert.assertEquals(1,requirementApprovalTransitions.size());
        Assert.assertEquals("ipc_review", requirementApprovalTransitions.get(0).getToState());
    }
}
