package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.command.download.DownloadIPCFinalisedCommand;
import fk.retail.ip.requirement.internal.command.download.GenerateExcelCommand;
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
public class IPCFinalisedCommandTest {
    @InjectMocks
    DownloadIPCFinalisedCommand downloadIPCFinalisedCommand;

    @Mock
    JPAFsnBandRepository fsnBandRepository;

    @Mock
    GenerateExcelCommand generateExcelCommand;

    @Mock
    WeeklySaleRepository weeklySaleRepository;

    @Mock
    ProductInfoRepository productInfoRepository;

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
    public void downloadTest() throws IOException {
        List<Requirement> requirements = getRequirements();
        Mockito.when(fsnBandRepository.fetchBandDataForFSNs(Mockito.anySetOf(String.class))).thenReturn(Arrays.asList(getFsnBand()));
        Mockito.when(weeklySaleRepository.fetchWeeklySalesForFsns(Mockito.anySetOf(String.class))).thenReturn(getWeeklySale());
        Mockito.when(warehouseRepository.fetchWarehouseNameByCode(Mockito.anySetOf(String.class))).thenReturn(getWarehouse());
        Mockito.when(productInfoRepository.getProductInfo(Mockito.anySet())).thenReturn(TestHelper.getProductInfo());
        Mockito.doReturn(TestHelper.getZuluData()).when(zuluClient).getRetailProductAttributes(Mockito.anyList());
        downloadIPCFinalisedCommand.execute(requirements,false);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/IPCFinalised.xlsx"));
        Assert.assertEquals(2, captor.getValue().size());

    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]", 2,3,4,5,6);
        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]", 7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.PROPOSED.toString(), true, snapshot, 21,
                "ABC",100,101,"INR", 3, "", "Daily planning" );

        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.PROPOSED.toString(), true, snapshot1, 22,
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

    private List<Warehouse> getWarehouse() {
        List<Warehouse> warehouses = Lists.newArrayList();
        Warehouse warehouse = TestHelper.getWarehouse("dummy_warehouse1","dummy_warehouse_name1");
        warehouses.add(warehouse);
        warehouse = TestHelper.getWarehouse("dummy_warehouse2","dummy_warehouse_name2");
        warehouses.add(warehouse);
        return warehouses;
    }

}
