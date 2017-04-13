package fk.retail.ip.requirement.internal.command.upload;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.FdpRequirementIngestorImpl;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by agarwal.vaibhav on 02/03/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class ProposedUploadCommandTest {

    @InjectMocks
    ProposedUploadCommand uploadProposedCommand;

    @Mock
    FdpRequirementIngestorImpl fdpRequirementIngestor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadTest() throws IOException {
        List<RequirementDownloadLineItem> requirementDownloadLineItems =
                TestHelper.getProposedRequirementDownloadLineItem();
        List<Requirement> requirements = getRequirements();
        List<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems = uploadProposedCommand.execute(requirementDownloadLineItems, requirements, "");

        Map<Long, Requirement> requirementMap = requirements.stream().collect
                (Collectors.toMap(Requirement::getId, Function.identity()));


        Assert.assertEquals(20, (int)requirementMap.get((long)1).getQuantity());
        Assert.assertEquals("{\"quantityOverrideComment\":\"test_ipc\"}",
                requirementMap.get((long)1).getOverrideComment());
        Assert.assertEquals(100, (int)requirementMap.get((long)2).getQuantity());
        Assert.assertEquals(100, (int)requirementMap.get((long)3).getQuantity());
        Assert.assertEquals(0, (int)requirementMap.get((long)4).getQuantity());

        Assert.assertEquals(Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING,
                uploadOverrideFailureLineItems.get(0).getFailureReason());
        Assert.assertEquals(Constants.FSN_OR_WAREHOUSE_IS_MISSING,
                uploadOverrideFailureLineItems.get(1).getFailureReason());
    }


    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2, 3, 4, 5, 6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]",7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();

        Requirement requirement = TestHelper.getRequirement(
                "dummy_fsn",
                "dummy_warehouse_1",
                RequirementApprovalState.PROPOSED.toString(),
                true,
                snapshot,
                100,
                "ABC",
                100,
                101,
                "INR",
                3,
                "",
                "Daily planning"
        );
        requirement.setId((long) 1);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "dummy_fsn",
                "dummy_warehouse_2",
                RequirementApprovalState.PROPOSED.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 2);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "dummy_fsn_1",
                "dummy_warehouse_1",
                RequirementApprovalState.PROPOSED.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 3);
        requirements.add(requirement);

        requirement = TestHelper.getRequirement(
                "dummy_fsn_1",
                "dummy_warehouse_2",
                RequirementApprovalState.PROPOSED.toString(),
                true,
                snapshot1,
                100,
                "DEF",
                10,
                9,
                "USD",
                4,
                "",
                "Daily planning"
        );
        requirement.setId((long) 4);
        requirements.add(requirement);
        return requirements;
    }

}
