package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import java.util.List;
import java.util.Map;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class RopRocApplicatorTest {

    @Inject
    ObjectMapper objectMapper;
    @Mock
    ForecastContext forecastContext;
    @Mock
    OnHandQuantityContext onHandQuantityContext;
    RopRocApplicator ropRocApplicator;

    String policyFormat = "\"%s\":{\"days\":\"%d\",\"quantity\":0}";
    int maxdays = Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        List<Double> forecast = Lists.newArrayList(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0);
        Mockito.when(forecastContext.getForecast(Matchers.anyString(), Matchers.anyString())).thenReturn(forecast);
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString(), Matchers.anyString())).thenReturn(0.0);
        ropRocApplicator = new RopRocApplicator(objectMapper);
    }

    @Test
    public void testInvalidRopPolicy() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1, requirement2, requirement3);
        Map<PolicyType, String> policyMap = Maps.newHashMap();
        //rop > maxdays
        String rop2 = String.format(policyFormat, "wh2", maxdays + 1);
        //rop < 0
        String rop3 = String.format(policyFormat, "wh3", -1);
        //rop for wh1 is not present
        policyMap.put(PolicyType.ROP, "{" + rop2 + "," + rop3 + "}");
        ropRocApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Lists.newArrayList(requirement2, requirement3).forEach(requirement -> {
            Assert.assertEquals("error", requirement.getState());
            Assert.assertEquals("Valid Rop policy for this fsn is not present", requirement.getOverrideComment());
        });
    }

    @Test
    public void testInvalidRocPolicy() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1, requirement2, requirement3);
        Map<PolicyType, String> policyMap = Maps.newHashMap();
        int rop = 40;
        String rop1 = String.format(policyFormat, "wh1", rop);
        String rop2 = String.format(policyFormat, "wh2", rop);
        String rop3 = String.format(policyFormat, "wh3", rop);
        policyMap.put(PolicyType.ROP, "{" + rop1 + "," + rop2 + "," + rop3 + "}");
        //roc < 0
        String roc2 = String.format(policyFormat, "wh2", -1);
        //roc < rop
        String roc3 = String.format(policyFormat, "wh3", rop - 1);
        //rop for wh1 is not present
        policyMap.put(PolicyType.ROC, "{" + roc2 + "," + roc3 + "}");
        ropRocApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Lists.newArrayList(requirement2, requirement3).forEach(requirement -> {
            Assert.assertEquals("error", requirement.getState());
            Assert.assertEquals("Valid Roc policy for this fsn is not present", requirement.getOverrideComment());
        });
    }

    @Test
    public void testPolicyApplication() {
        List<Requirement> requirements = Lists.newArrayList();
        Map<PolicyType, String> policyMap = Maps.newHashMap();
        //basic case
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        requirements.add(requirement1);
        String rop1 = String.format(policyFormat, "wh1", 42);
        String roc1 = String.format(policyFormat, "wh1", 45);
        //both 0
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        requirements.add(requirement2);
        String rop2 = String.format(policyFormat, "wh2", 0);
        String roc2 = String.format(policyFormat, "wh2", 0);
        //both max
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 0.0, null, 0, 0, null, 0, null, null);
        requirements.add(requirement3);
        String rop3 = String.format(policyFormat, "wh3", maxdays);
        String roc3 = String.format(policyFormat, "wh3", maxdays);

        policyMap.put(PolicyType.ROP, "{" + rop1 + "," + rop2 + "," + rop3 + "}");
        policyMap.put(PolicyType.ROC, "{" + roc1 + "," + roc2 + "," + roc3 + "}");
        ropRocApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);

        Assert.assertEquals(24, requirement1.getQuantity(), 0.01);
        Assert.assertEquals("{\"Rop\":42.00},{\"Roc\":45.00}", requirement1.getRequirementSnapshot().getPolicy());
        Assert.assertEquals(0, requirement2.getQuantity(), 0.01);
        Assert.assertEquals(120, requirement3.getQuantity(), 0.01);

        //testing effect of on hand quantity
        //rop < on hand
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString(), Matchers.anyString())).thenReturn(22.0);
        requirement1.setQuantity(0);
        ropRocApplicator.applyPolicies("fsn1", Lists.newArrayList(requirement1), policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(0, requirement1.getQuantity(), 0.01);

        //rop = on hand
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString(), Matchers.anyString())).thenReturn(21.0);
        requirement1.setQuantity(0);
        ropRocApplicator.applyPolicies("fsn1", Lists.newArrayList(requirement1), policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(3, requirement1.getQuantity(), 0.01);

        //rop > on hand
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString(), Matchers.anyString())).thenReturn(20.0);
        requirement1.setQuantity(0);
        ropRocApplicator.applyPolicies("fsn1", Lists.newArrayList(requirement1), policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(4, requirement1.getQuantity(), 0.01);

        //error state should be ignored
        requirement1.setQuantity(0);
        requirement1.setState(Constants.ERROR_STATE);
        ropRocApplicator.applyPolicies("fsn1", Lists.newArrayList(requirement1), policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(0, requirement1.getQuantity(), 0.01);
    }
}
