package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.proc.internal.command.PushToProcClient;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by yogeshwari.k on 20/04/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class PushToProcCommandTest {

    @InjectMocks
    PushToProcCommand pushToProcCommand;

    @Mock
    RequirementRepository requirementRepository;

    @Mock
    PushToProcClient pushToProcClient;

    @Mock
    FdpRequirementIngestorImpl fdpRequirementIngestor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPushToProc() throws IOException{
        List<Requirement> requirements = getRequirements();
        String userId = "user1";
        int pushedRequirements = pushToProcCommand.pushToProc(requirements,userId);
        Assert.assertEquals(1, pushedRequirements);
    }

    private List<Requirement> getRequirements()
    {
        List<Requirement> requirements = Lists.newArrayList();
        RequirementSnapshot requirementSnapshot = TestHelper.getRequirementSnapshot("[1,2,3]",10,10,10,10,10);
        Requirement requirement1 = TestHelper.getRequirement("fsn1","wh1","ipc_finalized",true,requirementSnapshot,50,"supplier1",100,110,"INR",5,"comment1","DAILY PLANNING");
        Requirement requirement2 = TestHelper.getRequirement("fsn2","wh2","ipc_finalized",true,requirementSnapshot,0,"supplier1",100,110,"INR",5,"comment1","DAILY PLANNING");
        Requirement requirement3 = TestHelper.getRequirement("fsn3","wh1","ipc_finalized",true,requirementSnapshot,50,null,100,110,"INR",5,"comment1","DAILY PLANNING");
        Requirement requirement4 = TestHelper.getRequirement("fsn4","wh1","ipc_finalized",true,requirementSnapshot,50,"-",100,110,"INR",5,"comment1","DAILY PLANNING");
        Requirement requirement5 = TestHelper.getRequirement("fsn6","wh3","ipc_finalized",true,requirementSnapshot,50,"  ",100,110,"INR",5,"comment1","DAILY PLANNING");
        requirements.add(requirement1);
        requirements.add(requirement2);
        requirements.add(requirement3);
        requirements.add(requirement4);
        requirements.add(requirement5);
        return requirements;
    }

}
