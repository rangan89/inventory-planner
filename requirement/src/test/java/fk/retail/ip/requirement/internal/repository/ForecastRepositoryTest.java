package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Forecast;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class ForecastRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    ForecastRepository forecastRepository;

    @Test
    public void testFetchByFsns() {
        Forecast forecast1 = TestHelper.getForecast("fsn1", "wh1", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
        Forecast forecast2 = TestHelper.getForecast("fsn1", "wh2", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
        Forecast forecast3 = TestHelper.getForecast("fsn2", "wh1", "[13.49,13.49,13.01,12.49,12.49,11.99,11.99,11.99,11.50,11.50,11.50,10.99,10.99,10.99,10.99]");
        forecastRepository.persist(forecast1);
        forecastRepository.persist(forecast2);
        forecastRepository.persist(forecast3);
        List<Forecast> policies = forecastRepository.fetchByFsns(Sets.newHashSet("fsn1"));
        Assert.assertEquals(2, policies.size());
        Assert.assertEquals(forecast1, policies.get(0));
        Assert.assertEquals(forecast2, policies.get(1));
    }
}
