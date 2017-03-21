package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class ApprovalServiceTest extends TransactionalJpaRepositoryTest {

    @Inject
    private RequirementRepository requirementRepository;

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Test
    public void testProposedStateForwardFlow() {
        testForwardFlow("proposed", "verified", "verify");
    }

    @Test
    public void testVerifiedStateForwardFlow() {
        testForwardFlow("verified", "approved", "approve");
    }

    @Test
    public void testApprovedStateForwardFlow() {
        testForwardFlow("approved", "bd_approved", "bd_approve");
    }

    @Test
    public void testBdApprovedStateForwardFlow() {
        testForwardFlow("bd_approved", "bizfin_approved", "bizfin_approve");
    }

    @Test
    public void testBizFinApprovedStateForwardFlow() {
        testForwardFlow("bizfin_approved", "ipc_finalized", "ipc_finalize");
    }

    public void testVerifiedStateBackwardFlow() {
        testBackwardFlow("verified", "proposed", "cancel_verify");
    }

    @Test
    public void testApprovedStateBackwardFlow() {
        testBackwardFlow("approved", "verified", "cancel_approve");
    }

    @Test
    public void testBdApprovedStateBackwardFlow() {
        testBackwardFlow("bd_approved", "approved", "cancel_bd_approve");
    }

    @Test
    public void testBizFinApprovedStateBackwardFlow() {
        testBackwardFlow("bizfin_approved", "bd_approved", "cancel_bizfin_approve");
    }

    private void testForwardFlow(String fromState, String toState, String action) {
        Requirement requirement = createRequirement(fromState);
        Requirement cdoRequirement = createRequirement(RequirementApprovalState.CDO_REVIEW.toString());
        ApprovalService service = new ApprovalService("/requirement-state-actions.json");
        Function<Requirement, String> getter = Requirement::getState;
        service.changeState(Arrays.asList(requirement), "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));

        List<Requirement> results = requirementRepository.findEnabledRequirementsByStateFsn(toState, Arrays.asList(requirement.getFsn()));
        Requirement actual = results.get(0);
        Assert.assertEquals(toState, actual.getState());
        Assert.assertTrue(actual.isCurrent());
        Assert.assertTrue(actual.isEnabled());
        Assert.assertEquals(actual.getPreviousStateId(), requirement.getId());

        Assert.assertEquals(actual.getQuantity(), cdoRequirement.getQuantity(),0.01);

        Assert.assertEquals(actual.getSla(), requirement.getSla());
        Assert.assertEquals(actual.getSupplier(), requirement.getSupplier());
        Assert.assertEquals(actual.getApp(), requirement.getApp());

        Assert.assertFalse(requirement.isCurrent());
        Assert.assertTrue(requirement.isEnabled());
    }

    private void testBackwardFlow(String fromState, String toState, String action) {
        createRequirement(toState); // create the previous state first
        Requirement requirement = createRequirement(fromState);
        ApprovalService service = new ApprovalService("/requirement-state-actions.json");
        Function<Requirement, String> getter = Requirement::getState;
        service.changeState(Arrays.asList(requirement), "userId", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));

        List<Requirement> results = requirementRepository.findEnabledRequirementsByStateFsn(toState, Arrays.asList(requirement.getFsn()));
        Requirement actual = results.get(0);
        Assert.assertEquals(toState, actual.getState());
        Assert.assertTrue(actual.isCurrent());
        Assert.assertTrue(actual.isEnabled());
//        Assert.assertEquals(actual.getPreviousStateId(), requirement.getId());
        Assert.assertEquals(actual.getQuantity(), requirement.getQuantity(), 0.01);
        Assert.assertEquals(actual.getSla(), requirement.getSla());
        Assert.assertEquals(actual.getSupplier(), requirement.getSupplier());
        Assert.assertEquals(actual.getApp(), requirement.getApp());

        Assert.assertFalse(requirement.isCurrent());
        Assert.assertTrue(requirement.isEnabled());
    }

    private Requirement createRequirement(String state) {
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn1");
        requirement.setState(state);
        requirement.setEnabled(true);
        requirement.setCurrent(true);
        requirement.setWarehouse("dummy_warehouse");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());

        Long projectionId = insertProjection("fsn1", state).longValue();
        requirement.setProjectionId(projectionId);
        requirementRepository.persist(requirement);
        return requirement;
    }
    private Requirement createCdoRequirement(String state) {
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn1");
        requirement.setState(state);
        requirement.setEnabled(true);
        requirement.setCurrent(false);
        requirement.setWarehouse("dummy_warehouse");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());

        Long projectionId = insertProjection("fsn1", state).longValue();
        requirement.setProjectionId(projectionId);
        requirementRepository.persist(requirement);
        return requirement;
    }
    //TODO: legacy code
    private BigInteger insertProjection(String fsn, String state) {
        EntityManager entityManager = entityManagerProvider.get();
        try {
            return (BigInteger) entityManager
                    .createNativeQuery("SELECT id FROM projections WHERE fsn=:fsn")
                    .setParameter("fsn", fsn)
                    .getSingleResult();
        } catch (NoResultException ex) {
            entityManager
                    .createNativeQuery("INSERT INTO projections(fsn, current_state, version) VALUES( :fsn, :state, 0)")
                    .setParameter("fsn", fsn)
                    .setParameter("state", state)
                    .executeUpdate();
            return (BigInteger) entityManager
                    .createNativeQuery("SELECT id FROM projections WHERE fsn=:fsn")
                    .setParameter("fsn", fsn)
                    .getSingleResult();
        }
    }

    //TODO: legacy code
    @Before
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS projections"
                + "  (ID BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL,"
                + "  FSN VARCHAR(32),"
                + "  current_state varchar(32) NOT NULL,"
                + "  PRIMARY KEY (ID))";
        EntityManager entityManager = entityManagerProvider.get();
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
