package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class ForecastContextTest {

    @Inject
    ObjectMapper objectMapper;
    ForecastContext forecastContext;

    @Before
    public void setup() {
        forecastContext = new ForecastContext(objectMapper);
        forecastContext.addForecast("fsn1", "wh1", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
        forecastContext.addForecast("fsn1", "wh2", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
        forecastContext.addForecast("fsn2", "wh2", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
    }

    @Test
    public void testAddForecast() {
        List<Double> expected = Lists.newArrayList(13.49, 13.49, 13.01, 12.49, 12.49, 11.99, 11.99, 11.99, 11.50, 11.50, 11.50, 10.99, 10.99, 10.99, 10.99);
        Assert.assertEquals(expected, forecastContext.getForecast("fsn1", "wh1"));
    }

    @Test
    public void testGetFsns() {
        Assert.assertEquals(Sets.newHashSet("fsn1","fsn2"), forecastContext.getFsns());
    }

}
