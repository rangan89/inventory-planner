package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.*;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import fk.retail.ip.zulu.client.ZuluClient;
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
public class DownloadIPCReviewCommandTest {
    @InjectMocks
    DownloadIPCReviewCommand downloadIPCReviewCommand;

    @Mock
    JPAFsnBandRepository fsnBandRepository;

    @Mock
    GenerateExcelCommand generateExcelCommand;

    @Mock
    WeeklySaleRepository weeklySaleRepository;

    @Mock
    ProductInfoRepository productInfoRepository;

    @Mock
    RequirementRepository requirementRepository;

    @Mock
    ZuluClient zuluClient;

    @Mock
    WarehouseRepository warehouseRepository;

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
        Mockito.when(warehouseRepository.fetchWarehouseNameByCode(Mockito.anySetOf(String.class))).thenReturn(getWarehouse());
        Mockito.when(productInfoRepository.getProductInfo(Mockito.anyList())).thenReturn(TestHelper.getProductInfo());
        Mockito.doReturn(TestHelper.getZuluData()).when(zuluClient).getRetailProductAttributes(Mockito.anyList());
        Mockito.when(requirementRepository.findEnabledRequirementsByStateFsn(Mockito.matches(RequirementApprovalState.BIZFIN_REVIEW.toString()),Mockito.anySetOf(String.class))).thenReturn(getBizFinData());
        Mockito.when(requirementRepository.findEnabledRequirementsByStateFsn(Mockito.matches(RequirementApprovalState.CDO_REVIEW.toString()),Mockito.anySetOf(String.class))).thenReturn(getCdoData());
        Mockito.when(requirementRepository.findEnabledRequirementsByStateFsn(Mockito.matches(RequirementApprovalState.PROPOSED.toString()),Mockito.anySetOf(String.class))).thenReturn(getIpcQuantity());

        downloadIPCReviewCommand.execute(requirements,false);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/IPCReview.xlsx"));
        Assert.assertEquals(2, captor.getValue().size());

        Assert.assertEquals("fsn", captor.getValue().get(0).getFsn());
        Assert.assertEquals("dummy_warehouse1", captor.getValue().get(0).getWarehouse());
        Assert.assertEquals(21,(int)captor.getValue().get(0).getQuantity());
        Assert.assertEquals("ABC", captor.getValue().get(0).getSupplier());
        Assert.assertEquals("bizfin_comment",captor.getValue().get(0).getBizFinComment());
        Assert.assertEquals(30,(int)captor.getValue().get(0).getBizFinRecommendedQuantity());
        Assert.assertEquals("cdo_comment",captor.getValue().get(0).getCdoOverrideReason());
        Assert.assertEquals(15,(int)captor.getValue().get(0).getIpcProposedQuantity());

        Assert.assertEquals("fsn", captor.getValue().get(1).getFsn());
        Assert.assertEquals("dummy_warehouse2", captor.getValue().get(1).getWarehouse());
        Assert.assertEquals(22,(int)captor.getValue().get(1).getQuantity());
        Assert.assertEquals("DEF", captor.getValue().get(1).getSupplier());
        Assert.assertEquals(22,(int)captor.getValue().get(1).getIpcProposedQuantity());
    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot, 30,
                "ABC",100,101,"INR", 3, "bizfin_comment", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot1, 22,
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
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();

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

    private List<Requirement> getBizFinData() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot, 30,
                "ABC",100,101,"INR", 3, "bizfin_comment", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.BIZFIN_REVIEW.toString(), true, snapshot, 22,
                "DEF",10,9,"USD", 4, "", "Daily planning" );


        requirements.add(requirement);

        return requirements;

    }

    private List<Requirement> getCdoData(){
        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);
        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.CDO_REVIEW.toString(), true, snapshot, 21,
                "ABC",100,101,"INR", 3, "cdo_comment", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.CDO_REVIEW.toString(), true, snapshot1, 22,
                "DEF",10,9,"USD", 4, "", "Daily planning" );

        requirements.add(requirement);

        return requirements;
    }

    private List<Requirement> getIpcQuantity() {
        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);
        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.PROPOSED.toString(), true, snapshot, 15,
                "ABC",100,101,"INR", 3, "", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.PROPOSED.toString(), true, snapshot1, 22,
                "DEF",10,9,"USD", 4, "", "Daily planning" );

        requirements.add(requirement);

        return requirements;
    }

    private List<Warehouse> getWarehouse() {
        List<Warehouse> warehouses = Lists.newArrayList();
        Warehouse warehouse = TestHelper.getWarehouse("dummy_warehouse1","dummy_warehouse_name1");
        warehouses.add(warehouse);
        warehouse = TestHelper.getWarehouse("dummy_warehouse2","dummy_warehouse_name2");
        warehouses.add(warehouse);
        return warehouses;
    }

}
