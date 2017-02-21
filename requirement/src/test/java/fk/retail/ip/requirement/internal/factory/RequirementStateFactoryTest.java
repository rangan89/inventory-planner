package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.exception.InvalidRequirementStateException;
import fk.retail.ip.requirement.internal.states.BizFinRequirementState;
import fk.retail.ip.requirement.internal.states.CDOReviewRequirementState;
import fk.retail.ip.requirement.internal.states.IPCFinalisedRequirementState;
import fk.retail.ip.requirement.internal.states.IPCReviewRequirementState;
import fk.retail.ip.requirement.internal.states.ProposedRequirementState;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class RequirementStateFactoryTest {

    @Inject
    RequirementStateFactory requirementStateFactory;

    @Test(expected = InvalidRequirementStateException.class)
    public void shouldThrowInvalidRequirementStateExceptionOnInvalidStateTest() {
        requirementStateFactory.getRequirementState("invalidState");
    }

    @Test
    public void testGetProposedRequirementState() {
        Assert.assertEquals(requirementStateFactory.getRequirementState("proposed").getClass().getName(),(ProposedRequirementState.class.getName()));
    }

    @Test
    public void testGetCdoReviewRequirementState() {
        Assert.assertEquals(requirementStateFactory.getRequirementState("CDOReview").getClass().getName(),(CDOReviewRequirementState.class.getName()));
    }

    @Test
    public void testGetBizFinReviewRequirementState() {
        Assert.assertEquals(requirementStateFactory.getRequirementState("BizFinReview").getClass().getName(),(BizFinRequirementState.class.getName()));
    }

    @Test
    public void testGetIPCReviewRequirementState() {
        Assert.assertEquals(requirementStateFactory.getRequirementState("IPCReview").getClass().getName(),(IPCReviewRequirementState.class.getName()));
    }

    @Test
    public void testGetIpcFinalisedRequirementState() {
        Assert.assertEquals(requirementStateFactory.getRequirementState("IPCFinalised").getClass().getName(),(IPCFinalisedRequirementState.class.getName()));
    }


}
