package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.repository.JPAFsnBandRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by nidhigupta.m on 15/02/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class DownloadProposedCommandTest {

    @InjectMocks
    DownloadProposedCommand downloadProposedCommand;

    @Mock
    JPAFsnBandRepository fsnBandRepository;

    @Mock
    GenerateExcelCommand generateExcelCommand;

    @Mock
    WeeklySaleRepository weeklySaleRepository;

    @Captor
    private ArgumentCaptor<List<RequirementDownloadLineItem>> captor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void downloadTest() throws IOException {
        List<Requirement> requirements = getRequirements();
        Mockito.when(fsnBandRepository.fetchBandDataForFSNs(Mockito.anySetOf(String.class))).thenReturn(Arrays.asList(getFsnBand()));
        Mockito.when(weeklySaleRepository.fetchWeeklySalesForFsns(Mockito.anySetOf(String.class))).thenReturn(getWeeklySale());
        downloadProposedCommand.execute(requirements, false);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/proposed.xlsx"));
        Assert.assertEquals(2, captor.getValue().size());

        Assert.assertEquals("fsn", captor.getValue().get(0).getFsn());
        Assert.assertEquals("dummy_warehouse1", captor.getValue().get(0).getWarehouse());
        Assert.assertEquals(2, captor.getValue().get(0).getSalesBand());
        Assert.assertEquals(3, captor.getValue().get(0).getPvBand());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek0Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek1Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek2Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek3Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek4Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek5Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek6Sale());
        Assert.assertEquals(20, captor.getValue().get(0).getWeek7Sale());
        Assert.assertEquals(2, captor.getValue().get(0).getInventory());
        Assert.assertEquals(3, captor.getValue().get(0).getQoh());
        Assert.assertEquals("[1,2]", captor.getValue().get(0).getForecast());
        Assert.assertEquals(4, captor.getValue().get(0).getPendingPOQty());
        Assert.assertEquals(5, captor.getValue().get(0).getOpenReqQty());
        Assert.assertEquals(6, captor.getValue().get(0).getIwitIntransitQty());
        Assert.assertEquals(21, captor.getValue().get(0).getQuantity());
        Assert.assertEquals("ABC", captor.getValue().get(0).getSupplier());

        Assert.assertEquals("fsn", captor.getValue().get(1).getFsn());
        Assert.assertEquals("dummy_warehouse2", captor.getValue().get(1).getWarehouse());
        Assert.assertEquals(2, captor.getValue().get(1).getSalesBand());
        Assert.assertEquals(3, captor.getValue().get(1).getPvBand());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek0Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek1Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek2Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek3Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek4Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek5Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek6Sale());
        Assert.assertEquals(30, captor.getValue().get(1).getWeek7Sale());
        Assert.assertEquals(7, captor.getValue().get(1).getInventory());
        Assert.assertEquals(8, captor.getValue().get(1).getQoh());
        Assert.assertEquals("[3,4]", captor.getValue().get(1).getForecast());
        Assert.assertEquals(9, captor.getValue().get(1).getPendingPOQty());
        Assert.assertEquals(10, captor.getValue().get(1).getOpenReqQty());
        Assert.assertEquals(11, captor.getValue().get(1).getIwitIntransitQty());
        Assert.assertEquals(22, captor.getValue().get(1).getQuantity());
        Assert.assertEquals("DEF", captor.getValue().get(1).getSupplier());


    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = new RequirementSnapshot();
        snapshot.setForecast("[1,2]");
        snapshot.setInventoryQty(2);
        snapshot.setQoh(3);
        snapshot.setPendingPoQty(4);
        snapshot.setOpenReqQty(5);
        snapshot.setIwitIntransitQty(6);
        snapshot.setCreatedAt(new Date());

        RequirementSnapshot snapshot1 = new RequirementSnapshot();
        snapshot1.setForecast("[3,4]");
        snapshot1.setInventoryQty(7);
        snapshot1.setQoh(8);
        snapshot1.setPendingPoQty(9);
        snapshot1.setOpenReqQty(10);
        snapshot1.setIwitIntransitQty(11);
        snapshot1.setCreatedAt(new Date());

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn");
        requirement.setState("proposed");
        requirement.setEnabled(true);
        requirement.setWarehouse("dummy_warehouse1");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());
        requirement.setRequirementSnapshot(snapshot);
        requirement.setQuantity(21);
        requirement.setSupplier("ABC");
        requirement.setMrp(100);
        requirement.setApp(101);
        requirement.setCurrency("INR");
        requirement.setSla(3);
        requirement.setInternational(false);
        requirement.setOverrideComment("");
        requirement.setProcType("Daily planning");

        requirements.add(requirement);

        requirement = new Requirement();
        requirement.setFsn("fsn");
        requirement.setState("proposed");
        requirement.setEnabled(true);
        requirement.setWarehouse("dummy_warehouse2");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());
        requirement.setRequirementSnapshot(snapshot1);
        requirement.setQuantity(22);
        requirement.setSupplier("DEF");
        requirement.setMrp(10);
        requirement.setApp(9);
        requirement.setCurrency("USD");
        requirement.setSla(4);
        requirement.setInternational(true);
        requirement.setOverrideComment("comment");
        requirement.setProcType("Daily planning");
        requirements.add(requirement);
        return requirements;
    }

    private FsnBand getFsnBand() {
        FsnBand fsnBand = new FsnBand();
        fsnBand.setFsn("fsn");
        fsnBand.setSalesBand(2);
        fsnBand.setPvBand(3);
        fsnBand.setTimeFrame("Last 30 Days");
        fsnBand.setCreatedAt(new Date());
        return fsnBand;
    }

    private List<WeeklySale> getWeeklySale() {
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();

        List<WeeklySale> weeklySales = Lists.newArrayList();

        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {
            WeeklySale weeklySale = new WeeklySale("fsn", "dummy_warehouse1", currentWeek, 20);
            weeklySale.setCreatedAt(new Date());
            weeklySales.add(weeklySale);
        });
        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {
            WeeklySale weeklySale = new WeeklySale("fsn", "dummy_warehouse2", currentWeek, 30);
            weeklySale.setCreatedAt(new Date());
            weeklySales.add(weeklySale);
        });

        return weeklySales;

    }

}
