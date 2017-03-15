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
public class MaxCoverageApplicatorTest {


    @Inject
    ObjectMapper objectMapper;
    @Mock
    ForecastContext forecastContext;
    @Mock
    OnHandQuantityContext onHandQuantityContext;
    MaxCoverageApplicator maxCoverageApplicator;

    String policyFormat = "{\""+Constants.MAX_COVERAGE_KEY+"\":%d}";
    int maxdays = Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        List<Double> forecast = Lists.newArrayList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0);
        Mockito.when(forecastContext.getForecast(Matchers.anyString())).thenReturn(forecast);
        Mockito.when(onHandQuantityContext.getTotalQuantity(Matchers.anyString())).thenReturn(0.0);
        maxCoverageApplicator = new MaxCoverageApplicator(objectMapper);
    }

    @Test
    public void testMaxCoverage() {
        Requirement requirement1 = TestHelper.getRequirement("fsn1", "wh1", "proposed", true, new RequirementSnapshot(), 50, null, 0, 0, null, 0, null, null);
        Requirement requirement2 = TestHelper.getRequirement("fsn1", "wh2", "proposed", true, new RequirementSnapshot(), 30, null, 0, 0, null, 0, null, null);
        Requirement requirement3 = TestHelper.getRequirement("fsn1", "wh3", "proposed", true, new RequirementSnapshot(), 20, null, 0, 0, null, 0, null, null);
        Requirement requirement4 = TestHelper.getRequirement("fsn1", "wh4", "error", true, new RequirementSnapshot(), 10, null, 0, 0, null, 0, null, null);
        Requirement requirement5 = TestHelper.getRequirement("fsn2", "wh1", "proposed", true, new RequirementSnapshot(), 100, null, 0, 0, null, 0, null, null);
        List<Requirement> requirements = Lists.newArrayList(requirement1, requirement2, requirement3, requirement4, requirement5);

        Map<PolicyType, String> policyMap = Maps.newHashMap();
        //max coverage not reached
        String maxCoverage = String.format(policyFormat, 98);
        policyMap.put(PolicyType.MAX_COVERAGE, maxCoverage);
        maxCoverageApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        Assert.assertEquals(50, requirement1.getQuantity(), 0.01);
        Assert.assertEquals(30, requirement2.getQuantity(), 0.01);
        Assert.assertEquals(20, requirement3.getQuantity(), 0.01);
        Assert.assertEquals(100, requirement5.getQuantity(), 0.01);

        requirement4.setState("proposed");
        maxCoverageApplicator.applyPolicies("fsn1", requirements, policyMap, forecastContext, onHandQuantityContext);
        double totalQuantity = requirement1.getQuantity()+requirement2.getQuantity()+requirement3.getQuantity()+requirement4.getQuantity();
        Assert.assertEquals(105, totalQuantity, 0.01);
    }
}
