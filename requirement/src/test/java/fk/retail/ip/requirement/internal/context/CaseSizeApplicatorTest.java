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
public class CaseSizeApplicatorTest {


    @Inject
    ObjectMapper objectMapper;
    @Mock
    ForecastContext forecastContext;
    @Mock
    OnHandQuantityContext onHandQuantityContext;
    CaseSizeApplicator caseSizeApplicator;

    String policyFormat = "{\""+Constants.CASE_SIZE_KEY+"\":%d}";
    int maxdays = Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        List<Double> forecast = Lists.newArrayList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0);
        Mockito.when(forecastContext.getForecast(Matchers.anyString())).thenReturn(forecast);
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString())).thenReturn(0.0);
        caseSizeApplicator = new CaseSizeApplicator(objectMapper);
    }

    @Test
    public void testCaseSize() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 24, null, 0, 0, null, 0, null, null);
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 25, null, 0, 0, null, 0, null, null);
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 26, null, 0, 0, null, 0, null, null);
        Requirement requirement4 = TestHelper.getRequirement("fsn1", "wh4", "error", true, new RequirementSnapshot(), 0, null, 0, 0, null, 0, null, null);
        Requirement requirement5 = TestHelper.getRequirement("fsn1", "wh4", "error", true, new RequirementSnapshot(), 50, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1, requirement2, requirement3, requirement4, requirement5);

        Map<PolicyType, String> policyMap = Maps.newHashMap();
        String caseSize = String.format(policyFormat, 50);
        policyMap.put(PolicyType.CASE_SIZE, caseSize);
        caseSizeApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(0, requirement1.getQuantity(), 0.01);
        Assert.assertEquals(50, requirement2.getQuantity(), 0.01);
        Assert.assertEquals(50, requirement3.getQuantity(), 0.01);
        Assert.assertEquals(0, requirement4.getQuantity(), 0.01);
        Assert.assertEquals(50, requirement5.getQuantity(), 0.01);
    }


    @Test
    public void testCaseSizeWithMaxCoverage() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 24, null, 0, 0, null, 0, null, null);
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 25, null, 0, 0, null, 0, null, null);
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 26, null, 0, 0, null, 0, null, null);
        Requirement requirement4 = TestHelper.getRequirement("fsn1", "wh4", "error", true, new RequirementSnapshot(), 0, null, 0, 0, null, 0, null, null);
        Requirement requirement5 = TestHelper.getRequirement("fsn1", "wh4", "error", true, new RequirementSnapshot(), 50, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1, requirement2, requirement3, requirement4, requirement5);

        Map<PolicyType, String> policyMap = Maps.newHashMap();
        String caseSize = String.format(policyFormat, 50);
        policyMap.put(PolicyType.CASE_SIZE, caseSize);
        String maxCoveragePolicyFormat = "{\""+Constants.MAX_COVERAGE_KEY+"\":%d}";
        String maxCoverage = String.format(maxCoveragePolicyFormat, 98);
        policyMap.put(PolicyType.MAX_COVERAGE, maxCoverage);
        caseSizeApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(0, requirement1.getQuantity(), 0.01);
        Assert.assertEquals(0, requirement2.getQuantity(), 0.01);
        Assert.assertEquals(0, requirement3.getQuantity(), 0.01);
        Assert.assertEquals(0, requirement4.getQuantity(), 0.01);
        Assert.assertEquals(50, requirement5.getQuantity(), 0.01);
    }

    @Test
    public void testInvalidCaseSize() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 24, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1);

        Map<PolicyType, String> policyMap = Maps.newHashMap();
        String caseSize = String.format(policyFormat, 0);
        policyMap.put(PolicyType.CASE_SIZE, caseSize);
        caseSizeApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(24, requirement1.getQuantity(), 0.01);

        caseSize = String.format(policyFormat, -1);
        policyMap.put(PolicyType.CASE_SIZE, caseSize);
        caseSizeApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(24, requirement1.getQuantity(), 0.01);
    }
}
