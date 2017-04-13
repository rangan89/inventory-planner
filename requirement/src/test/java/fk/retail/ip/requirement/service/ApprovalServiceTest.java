package fk.retail.ip.requirement.service;

import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementApprovalTransitionRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class ApprovalServiceTest extends TransactionalJpaRepositoryTest {

    @Mock
    private RequirementRepository requirementRepository;

    @Mock
    private RequirementApprovalTransitionRepository requirementApprovalStateTransitionRepository;

    @Mock
    FdpRequirementIngestorImpl fdpRequirementIngestor;

    @InjectMocks
    private ApprovalService approvalService;


    @Captor
    private ArgumentCaptor<Requirement> captor;


    @Test(expected = IllegalStateException.class)
    public void testApprovalFlowWithInvalidState() {
        String fromState = "proposed";
        Requirement requirement = createRequirement("verified", true);
        Function<Requirement, String> getter = Requirement::getState;
        approvalService.changeState(Arrays.asList(requirement), fromState, "userId", true, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository, requirementApprovalStateTransitionRepository, fdpRequirementIngestor));

    }

    @Test
    public void testApprovalForwardFlow() {
        String fromState = RequirementApprovalState.PRE_PROPOSED.toString();
        String toState =  RequirementApprovalState.CDO_REVIEW.toString();
        boolean forward = true;
        Requirement requirement = createRequirement(fromState, true);
        Function<Requirement, String> getter = Requirement::getState;
        RequirementApprovalTransition requirementApprovalTransition = TestHelper.getRequirementApprovalTransition(256, fromState, toState, forward);
        Mockito.when(requirementApprovalStateTransitionRepository.getApprovalTransition(Mockito.anyString(), Mockito.eq(true))).thenReturn(Arrays.asList(requirementApprovalTransition));
        Mockito.when(requirementRepository.find(Arrays.asList("fsn1"), true)).thenReturn(Arrays.asList(requirement));
        Mockito.doNothing().when(requirementRepository).updateProjections(Mockito.anyList(), Mockito.anyMap());
        approvalService.changeState(Arrays.asList(requirement), fromState, "userId", true, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository, requirementApprovalStateTransitionRepository, fdpRequirementIngestor));
        Mockito.verify(requirementRepository).persist(captor.capture());
        Assert.assertEquals(toState,captor.getValue().getState());
        Assert.assertEquals(requirement.getId(), captor.getValue().getPreviousStateId());
        Assert.assertEquals(true, captor.getValue().isCurrent());
        Assert.assertEquals(false, requirement.isCurrent());
    }


    @Test
    public void testApprovalForwardFlowWithDefaultGroupTransition() {
        String fromState = RequirementApprovalState.PRE_PROPOSED.toString();
        String toState =  RequirementApprovalState.PROPOSED.toString();
        boolean forward = true;
        Requirement requirement = createRequirement(fromState, true);
        Function<Requirement, String> getter = Requirement::getState;
        RequirementApprovalTransition requirementApprovalTransition = TestHelper.getRequirementApprovalTransition(Constants.DEFAULT_TRANSITION_GROUP, fromState, toState, forward);
        Mockito.when(requirementApprovalStateTransitionRepository.getApprovalTransition(Mockito.anyString(), Mockito.eq(true))).thenReturn(Arrays.asList(requirementApprovalTransition));
        Mockito.when(requirementRepository.find(Arrays.asList("fsn1"), true)).thenReturn(Arrays.asList(requirement));
        Mockito.doNothing().when(requirementRepository).updateProjections(Mockito.anyList(), Mockito.anyMap());
        approvalService.changeState(Arrays.asList(requirement), fromState, "userId", true, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository, requirementApprovalStateTransitionRepository, fdpRequirementIngestor));
        Mockito.verify(requirementRepository).persist(captor.capture());
        Assert.assertEquals(toState,captor.getValue().getState());
        Assert.assertEquals(requirement.getId(), captor.getValue().getPreviousStateId());
        Assert.assertEquals(true, captor.getValue().isCurrent());
        Assert.assertEquals(false, requirement.isCurrent());
    }


    @Test
    public void testBackwardFlow() {
        String fromState = RequirementApprovalState.PROPOSED.toString();
        String toState =  RequirementApprovalState.PRE_PROPOSED.toString();
        boolean forward = false;
        Requirement requirement = createRequirement(fromState, true);
        Function<Requirement, String> getter = Requirement::getState;
        RequirementApprovalTransition requirementApprovalTransition = TestHelper.getRequirementApprovalTransition(Constants.DEFAULT_TRANSITION_GROUP, fromState, toState, forward);
        Mockito.when(requirementApprovalStateTransitionRepository.getApprovalTransition(Mockito.anyString(), Mockito.eq(true))).thenReturn(Arrays.asList(requirementApprovalTransition));
        Mockito.doNothing().when(requirementRepository).updateProjections(Mockito.anyList(), Mockito.anyMap());
        List<Requirement> allEnabledRequirements = Arrays.asList(createRequirement(toState, false));
        Mockito.when(requirementRepository.find(Arrays.asList("fsn1"), true)).thenReturn(allEnabledRequirements);
        approvalService.changeState(Arrays.asList(requirement), fromState, "userId", true, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository, requirementApprovalStateTransitionRepository, fdpRequirementIngestor));
        Assert.assertEquals(false, requirement.isCurrent());
    }


    @Test
    public void testForwardFlowWithToStateEntity() {
        String fromState = RequirementApprovalState.PRE_PROPOSED.toString();
        String toState =  RequirementApprovalState.PROPOSED.toString();
        boolean forward = true;
        Requirement requirement = createRequirement(fromState, true);
        Function<Requirement, String> getter = Requirement::getState;
        RequirementApprovalTransition requirementApprovalTransition = TestHelper.getRequirementApprovalTransition(Constants.DEFAULT_TRANSITION_GROUP, fromState, toState, forward);
        Mockito.when(requirementApprovalStateTransitionRepository.getApprovalTransition(Mockito.anyString(), Mockito.eq(true))).thenReturn(Arrays.asList(requirementApprovalTransition));
        List<Requirement> allEnabledRequirements = Arrays.asList(createRequirement(toState, false));
        Mockito.doNothing().when(requirementRepository).updateProjections(Mockito.anyList(), Mockito.anyMap());
        Mockito.when(requirementRepository.find(Arrays.asList("fsn1"), true)).thenReturn(allEnabledRequirements);
        approvalService.changeState(Arrays.asList(requirement), fromState, "userId", true, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository, requirementApprovalStateTransitionRepository, fdpRequirementIngestor));
        Assert.assertEquals(false, requirement.isCurrent());
    }


    private Requirement createRequirement(String state, boolean current) {
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn1");
        requirement.setState(state);
        requirement.setEnabled(true);
        requirement.setCurrent(current);
        requirement.setWarehouse("dummy_warehouse");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());
        Group group = TestHelper.getGroup("test_group");
        group.setId(256l);
        RequirementSnapshot requirementSnapshot = new RequirementSnapshot();
        requirementSnapshot.setGroup(group);
        requirement.setRequirementSnapshot(requirementSnapshot);
        return requirement;
    }

    //TODO: legacy code

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }



}
