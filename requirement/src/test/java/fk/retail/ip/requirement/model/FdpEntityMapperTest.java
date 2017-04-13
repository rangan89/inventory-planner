package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.FdpEntityPayload;
import fk.retail.ip.fdp.model.FdpRequirementEntityData;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

/**
 * Created by yogeshwari.k on 27/03/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class FdpEntityMapperTest {
    @Inject
    RequirementToFdpEntityMapper fdpEntityMapper;

    @Inject
    FdpConfiguration fdpConfiguration;

    @Test
    public void convertToEntityPayloadTest() {
        Requirement requirement = getRequirement();
        String requirementId= getRequirementId(requirement);
        FdpEntityPayload<FdpRequirementEntityData> fdpEntityPayload = fdpEntityMapper.convertToEntityPayload(requirementId,requirement);
        Assert.assertEquals(requirementId,fdpEntityPayload.getEntityId());
        Assert.assertEquals(fdpConfiguration.getRequirementEntitySchemaVersion(),fdpEntityPayload.getSchemaVersion());
        Assert.assertEquals(requirement.getUpdatedAt(),fdpEntityPayload.getUpdatedAt());
    }

    private Requirement getRequirement(){
        List<String> policyIdList1 = Lists.newArrayList();
        String policyId1 = "dummy_group_fsn1_warehouse1_Rop";
        policyIdList1.add(policyId1);
        policyId1 = "dummy_group_fsn1_warehouse1_Roc";
        policyIdList1.add(policyId1);
        String policyIds = String.join(",", policyIdList1);

        RequirementSnapshot requirementSnapshot1 = new RequirementSnapshot();
        requirementSnapshot1.setForecast("[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]");
        requirementSnapshot1.setInventoryQty(10);
        requirementSnapshot1.setPendingPoQty(10);
        requirementSnapshot1.setOpenReqQty(10);
        requirementSnapshot1.setIwitIntransitQty(10);

        Requirement requirement1 = new Requirement();
        requirement1.setFsn("fsn1");
        requirement1.setWarehouse("warehouse1");
        Date date1 = new Date();
        requirement1.setCreatedAt(date1);
        requirement1.setUpdatedAt(date1);
        requirement1.setQuantity(100);
        requirement1.setRequirementSnapshot(requirementSnapshot1);
        requirement1.setSupplier("supplier1");
        requirement1.setApp(10);
        requirement1.setMrp(10);
        requirement1.setCurrency("INR");
        requirement1.setSla(5);
        requirement1.setState("proposed");
        requirement1.setEnabled(true);
        return requirement1;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

}
