package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.repository.JPAFsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
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
 * Created by yogeshwari.k on 19/02/17.
 */

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class DownloadCDOReviewCommandTest {
    @InjectMocks
    DownloadCDOReviewCommand downloadCDOReviewCommand;

    @Mock
    JPAFsnBandRepository fsnBandRepository;

    @Mock
    GenerateExcelCommand generateExcelCommand;

    @Mock
    WeeklySaleRepository weeklySaleRepository;

    @Mock
    LastAppSupplierRepository lastAppSupplierRepository;

    @Mock
    RequirementRepository requirementRepository;

    @Captor
    private ArgumentCaptor<List<RequirementDownloadLineItem>> captor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void downloadTestWithoutLastAppSupplier() throws IOException {
        List<Requirement> requirements = getRequirements();
        Mockito.when(fsnBandRepository.fetchBandDataForFSNs(Mockito.anySetOf(String.class))).thenReturn(Arrays.asList(getFsnBand()));
        Mockito.when(weeklySaleRepository.fetchWeeklySalesForFsns(Mockito.anySetOf(String.class))).thenReturn(getWeeklySale());
        Mockito.when(lastAppSupplierRepository.fetchLastAppSupplierForFsns(Mockito.anySetOf(String.class))).thenReturn(getLasAppSupplier());
        Mockito.when(requirementRepository.findEnabledRequirementsByStateFsn(Mockito.anyString(),Mockito.anySetOf(String.class))).thenReturn(getBizFinData());
        downloadCDOReviewCommand.execute(requirements,false);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/CDOReview.xlsx"));
        Assert.assertEquals(2, captor.getValue().size());

        Assert.assertEquals("fsn", captor.getValue().get(0).getFsn());
        Assert.assertEquals("dummy_warehouse1", captor.getValue().get(0).getWarehouse());
        Assert.assertEquals(2, (int)captor.getValue().get(0).getSalesBand());
        Assert.assertEquals(3, (int)captor.getValue().get(0).getPvBand());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek0Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek1Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek2Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek3Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek4Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek5Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek6Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek7Sale());
        Assert.assertEquals(2, (int)captor.getValue().get(0).getInventory());
        Assert.assertEquals(3, (int)captor.getValue().get(0).getQoh());
        Assert.assertEquals("[1,2]", captor.getValue().get(0).getForecast());
        Assert.assertEquals(15, (int)captor.getValue().get(0).getIntransitQty());
        Assert.assertEquals(21,(int)captor.getValue().get(0).getQuantity());
        Assert.assertEquals("ABC", captor.getValue().get(0).getSupplier());
        Assert.assertEquals("bizfin_comment",captor.getValue().get(0).getBizFinComment());
        Assert.assertEquals(30,(int)captor.getValue().get(0).getBizFinRecommendedQuantity());

        Assert.assertEquals("fsn", captor.getValue().get(1).getFsn());
        Assert.assertEquals("dummy_warehouse2", captor.getValue().get(1).getWarehouse());
        Assert.assertEquals(2, (int)captor.getValue().get(1).getSalesBand());
        Assert.assertEquals(3, (int)captor.getValue().get(1).getPvBand());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek0Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek1Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek2Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek3Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek4Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek5Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek6Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek7Sale());
        Assert.assertEquals(7, (int)captor.getValue().get(1).getInventory());
        Assert.assertEquals(8, (int)captor.getValue().get(1).getQoh());
        Assert.assertEquals("[3,4]", captor.getValue().get(1).getForecast());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getIntransitQty());
        Assert.assertEquals(22,(int)captor.getValue().get(1).getQuantity());
        Assert.assertEquals("DEF", captor.getValue().get(1).getSupplier());
    }

    @Test
    public void downloadTestWithLastAppSupplier() throws IOException {
        List<Requirement> requirements = getRequirements();
        Mockito.when(fsnBandRepository.fetchBandDataForFSNs(Mockito.anySetOf(String.class))).thenReturn(Arrays.asList(getFsnBand()));
        Mockito.when(weeklySaleRepository.fetchWeeklySalesForFsns(Mockito.anySetOf(String.class))).thenReturn(getWeeklySale());
        Mockito.when(lastAppSupplierRepository.fetchLastAppSupplierForFsns(Mockito.anySetOf(String.class))).thenReturn(getLasAppSupplier());
        Mockito.when(requirementRepository.findEnabledRequirementsByStateFsn(Mockito.anyString(),Mockito.anySetOf(String.class))).thenReturn(getBizFinData());
        downloadCDOReviewCommand.execute(requirements,true);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/CDOReviewWithLastAppSupplier.xlsx"));
        Assert.assertEquals(2, captor.getValue().size());

        Assert.assertEquals("fsn", captor.getValue().get(0).getFsn());
        Assert.assertEquals("dummy_warehouse1", captor.getValue().get(0).getWarehouse());
        Assert.assertEquals(2, (int)captor.getValue().get(0).getSalesBand());
        Assert.assertEquals(3, (int)captor.getValue().get(0).getPvBand());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek0Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek1Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek2Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek3Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek4Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek5Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek6Sale());
        Assert.assertEquals(20, (int)captor.getValue().get(0).getWeek7Sale());
        Assert.assertEquals(2, (int)captor.getValue().get(0).getInventory());
        Assert.assertEquals(3, (int)captor.getValue().get(0).getQoh());
        Assert.assertEquals("[1,2]", captor.getValue().get(0).getForecast());
        Assert.assertEquals(15, (int)captor.getValue().get(0).getIntransitQty());
        Assert.assertEquals(21,(int)captor.getValue().get(0).getQuantity());
        Assert.assertEquals("ABC", captor.getValue().get(0).getSupplier());
        Assert.assertEquals("supplier1",captor.getValue().get(0).getLastSupplier());
        Assert.assertEquals(100, (int)captor.getValue().get(0).getLastApp());
        Assert.assertEquals("bizfin_comment",captor.getValue().get(0).getBizFinComment());
        Assert.assertEquals(30,(int)captor.getValue().get(0).getBizFinRecommendedQuantity());

        Assert.assertEquals("fsn", captor.getValue().get(1).getFsn());
        Assert.assertEquals("dummy_warehouse2", captor.getValue().get(1).getWarehouse());
        Assert.assertEquals(2, (int)captor.getValue().get(1).getSalesBand());
        Assert.assertEquals(3, (int)captor.getValue().get(1).getPvBand());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek0Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek1Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek2Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek3Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek4Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek5Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek6Sale());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getWeek7Sale());
        Assert.assertEquals(7, (int)captor.getValue().get(1).getInventory());
        Assert.assertEquals(8, (int)captor.getValue().get(1).getQoh());
        Assert.assertEquals("[3,4]", captor.getValue().get(1).getForecast());
        Assert.assertEquals(30, (int)captor.getValue().get(1).getIntransitQty());
        Assert.assertEquals(22,(int)captor.getValue().get(1).getQuantity());
        Assert.assertEquals("DEF", captor.getValue().get(1).getSupplier());
        Assert.assertEquals("supplier2",captor.getValue().get(1).getLastSupplier());
        Assert.assertEquals(120, (int)captor.getValue().get(1).getLastApp());
    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", "cdo_review", true, snapshot, 21,
                "ABC",100,101,"INR", 3, "", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", "cdo_review", true, snapshot1, 22,
                "DEF",10,9,"USD", 4, "", "Daily planning" );

        requirements.add(requirement);

        return requirements;
    }

    private FsnBand getFsnBand() {
        FsnBand fsnBand = TestHelper.getFsnBand("fsn", "Last 30 Days");
        return fsnBand;
    }

    private List<WeeklySale> getWeeklySale() {
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();

        List<WeeklySale> weeklySales = Lists.newArrayList();

        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {
            WeeklySale weeklySale= TestHelper.getWeeklySale("fsn", "dummy_warehouse1", currentWeek, 20);
            weeklySales.add(weeklySale);
        });
        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {
            WeeklySale weeklySale= TestHelper.getWeeklySale("fsn", "dummy_warehouse2", currentWeek, 30);
            weeklySales.add(weeklySale);
        });

        return weeklySales;

    }

    private List<LastAppSupplier> getLasAppSupplier() {
        List<LastAppSupplier> lastAppSuppliers = Lists.newArrayList();
        LastAppSupplier lastAppSupplier = TestHelper.getLastAppSupplier("fsn","dummy_warehouse1","supplier1",100);
        lastAppSuppliers.add(lastAppSupplier);
        lastAppSupplier = TestHelper.getLastAppSupplier("fsn","dummy_warehouse2","supplier2",120);
        lastAppSuppliers.add(lastAppSupplier);
        return lastAppSuppliers;
    }

    private List<Requirement> getBizFinData() {
        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);
        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", "bizfin_review", true, snapshot, 30,
                "ABC",100,101,"INR", 3, "bizfin_comment", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", "bizfin_review", true, snapshot1, 22,
                "DEF",10,9,"USD", 4, "", "Daily planning" );


        requirements.add(requirement);

        return requirements;
    }

}
