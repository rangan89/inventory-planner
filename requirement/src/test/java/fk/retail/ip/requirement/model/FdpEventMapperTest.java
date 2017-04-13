package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.FdpEventPayload;
import fk.retail.ip.fdp.model.FdpRequirementEventData;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.command.PayloadCreationHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

/**
 * Created by yogeshwari.k on 28/03/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class FdpEventMapperTest {
    @Inject
    RequirementToFdpEventMapper fdpEventMapper;

    @Inject
    FdpConfiguration fdpConfiguration;

    @Test
    public void convertToEventPayloadTest() {
        Requirement requirement = getRequirement();
        String requirementId= getRequirementId(requirement);
        List<RequirementChangeMap> requirementChangeMapList = getRequirementChangeMapList(requirement);
        List<FdpEventPayload<FdpRequirementEventData>> fdpEventPayload = fdpEventMapper.convertToEventPayload(requirementId,requirementChangeMapList);
        Assert.assertEquals(fdpConfiguration.getRequirementEventSchemaVersion(),fdpEventPayload.get(0).getSchemaVersion());
        Assert.assertEquals(requirementId,fdpEventPayload.get(0).getData().getRequirementId());
    }

    private Requirement getRequirement(){
        Requirement requirement1 = new Requirement();
        requirement1.setFsn("fsn1");
        requirement1.setWarehouse("warehouse1");
        requirement1.setSla(5);
        Date date1 = new Date();
        requirement1.setCreatedAt(date1);
        return requirement1;
    }

    private String getRequirementId(Requirement requirement) {
        String requirementId = requirement.getFsn()+requirement.getWarehouse()+(requirement.getCreatedAt().toString());
        return requirementId;
    }

    private List<RequirementChangeMap> getRequirementChangeMapList(Requirement requirement) {
        List<RequirementChangeMap> requirementChangeMapList = Lists.newArrayList();
        requirementChangeMapList.add(PayloadCreationHelper.createChangeMap("Sla", requirement.getSla().toString(),"20", FdpRequirementEventType.CDO_SLA_OVERRIDE.toString(), "SLA overridden by CDO", "system"));
        return requirementChangeMapList;
    }


}
