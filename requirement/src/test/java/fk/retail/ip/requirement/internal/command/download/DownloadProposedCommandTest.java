package fk.retail.ip.requirement.internal.command.download;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.JPAFsnBandRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.TestHelper;
import fk.retail.ip.requirement.internal.repository.WarehouseRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;
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
    ZuluClient zuluClient;

    @Mock
    GenerateExcelCommand generateExcelCommand;

    @Mock
    ProductInfoRepository productInfoRepository;

    @Mock
    WeeklySaleRepository weeklySaleRepository;

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
        Mockito.when(fsnBandRepository.fetchBandDataForFSNs(Mockito.anySetOf(String.class))).thenReturn(Arrays.asList(TestHelper.getFsnBand("fsn", "Last 30 Days")));
        Mockito.when(weeklySaleRepository.fetchWeeklySalesForFsns(Mockito.anySetOf(String.class))).thenReturn(getWeeklySale());
        Mockito.when(warehouseRepository.fetchWarehouseNameByCode(Mockito.anySetOf(String.class))).thenReturn(getWarehouse());
        Mockito.when(productInfoRepository.getProductInfo(Mockito.anySet())).thenReturn(TestHelper.getProductInfo());
        Mockito.doReturn(TestHelper.getZuluData()).when(zuluClient).getRetailProductAttributes(Mockito.anyList());

        downloadProposedCommand.execute(requirements,false);
        Mockito.verify(generateExcelCommand).generateExcel(captor.capture(), Mockito.eq("/templates/proposed.xlsx"));

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
        Assert.assertEquals(21, (int)captor.getValue().get(0).getQuantity());
        Assert.assertEquals("ABC", captor.getValue().get(0).getSupplier());
        Assert.assertEquals("dummy_warehouse_name1", captor.getValue().get(0).getWarehouseName());

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
        Assert.assertEquals(22, (int)captor.getValue().get(1).getQuantity());
        Assert.assertEquals("DEF", captor.getValue().get(1).getSupplier());
        Assert.assertEquals("dummy_warehouse_name2", captor.getValue().get(1).getWarehouseName());

        /*
        * Check if db product data is fetched
        * */
        Assert.assertEquals("dummy_db_title", captor.getValue().get(0).getTitle());
        Assert.assertEquals("dummy_db_brand",captor.getValue().get(0).getBrand());
        Assert.assertEquals("dummy_db_vertical", captor.getValue().get(0).getVertical());
        Assert.assertEquals("dummy_db_category", captor.getValue().get(0).getCategory());
        Assert.assertEquals("dummy_db_super_category", captor.getValue().get(0).getSuperCategory());
        Assert.assertEquals(1, (int)captor.getValue().get(0).getFsp());

        /*
        * Check if zulu product data is fetched
        * */
//        Assert.assertEquals("dummy_zulu_title", captor.getValue().get(2).getTitle());
//        Assert.assertEquals("dummy_zulu_brand",captor.getValue().get(2).getBrand());
//        Assert.assertEquals("dummy_zulu_vertical", captor.getValue().get(2).getVertical());
//        Assert.assertEquals("dummy_zulu_category", captor.getValue().get(2).getCategory());
//        Assert.assertEquals("dummy_zulu_super_category", captor.getValue().get(2).getSuperCategory());
//        Assert.assertEquals(2, (int)captor.getValue().get(2).getFsp());

    }

    private List<Requirement> getRequirements() {

        RequirementSnapshot snapshot = TestHelper.getRequirementSnapshot("[1,2]",2,3,4,5,6);

        RequirementSnapshot snapshot1 = TestHelper.getRequirementSnapshot("[3,4]",7,8,9,10,11);

        List<Requirement> requirements = Lists.newArrayList();

        Requirement requirement = TestHelper.getRequirement("fsn", "dummy_warehouse1", RequirementApprovalState.PROPOSED.toString(), true, snapshot , 21, "ABC",
                100, 101, "INR", 3, "", "Daily planning");
        requirements.add(requirement);

        requirement = TestHelper.getRequirement("fsn", "dummy_warehouse2", RequirementApprovalState.PROPOSED.toString(), true, snapshot1 , 22, "DEF",
                10, 9, "USD", 4, "", "Daily planning");

        requirements.add(requirement);

//        requirement = TestHelper.getRequirement("zulufsn", "dummy_warehouse2",RequirementApprovalState.PROPOSED.toString(), true, snapshot1 , 22, "DEF",
//                10, 9, "USD", 4, "", "Daily planning");
//
//        requirements.add(requirement);

        return requirements;
    }

    private List<WeeklySale> getWeeklySale() {
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();

        List<WeeklySale> weeklySales = Lists.newArrayList();

        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {

            WeeklySale weeklySale = TestHelper.getWeeklySale("fsn", "dummy_warehouse1", currentWeek, 20);
            weeklySales.add(weeklySale);
        });
        IntStream.iterate(date.get(weekOfYear), currentWeek -> (currentWeek - 2 + 52) % 52 + 1).limit(8).forEach(currentWeek -> {
            WeeklySale weeklySale = TestHelper.getWeeklySale("fsn", "dummy_warehouse2", currentWeek, 30);
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
