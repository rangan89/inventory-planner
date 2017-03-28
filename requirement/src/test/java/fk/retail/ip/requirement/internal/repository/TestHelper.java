package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.internal.entities.Forecast;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.IwtRequest;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.retail.ip.requirement.internal.entities.WarehouseSupplierSla;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.internal.entities.EntityView;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHelper {

    public static IwtRequestItem getIwtRequestItem(String fsn, String status, IwtRequest iwtRequest) {
        IwtRequestItem iwtRequestItem = new IwtRequestItem();
        iwtRequestItem.setFsn(fsn);
        iwtRequestItem.setStatus(status);
        iwtRequestItem.setCreatedAt(new Date());
        iwtRequestItem.setIwtRequest(iwtRequest);
        return iwtRequestItem;
    }

    public static IwtRequest getIwtRequest(String externalId) {
        IwtRequest iwtRequest = new IwtRequest();
        iwtRequest.setExternalId(externalId);
        iwtRequest.setCreatedAt(new Date());
        return iwtRequest;
    }

    public static GroupFsn getGroupFsn(String fsn, Group group) {
        GroupFsn groupFsn = new GroupFsn();
        groupFsn.setFsn(fsn);
        groupFsn.setGroup(group);
        groupFsn.setCreatedAt(new Date());
        return groupFsn;
    }

    public static Group getEnabledGroup(String name) {
        Group group = new Group();
        group.setName(name);
        group.setEnabled(true);
        group.setCreatedAt(new Date());
        return group;
    }

    public static Group getDisabledGroup(String name) {
        Group group = new Group();
        group.setName(name);
        group.setEnabled(false);
        group.setCreatedAt(new Date());
        return group;
    }


    public static OpenRequirementAndPurchaseOrder getOpenRequirementAndPurchaseOrder() {
        OpenRequirementAndPurchaseOrder openRequirementAndPurchaseOrder = new OpenRequirementAndPurchaseOrder();
        openRequirementAndPurchaseOrder.setFsn("fsn1");
        openRequirementAndPurchaseOrder.setCreatedAt(new Date());
        return openRequirementAndPurchaseOrder;
    }

    public static Policy getPolicy(String fsn, Group group) {
        Policy policy = new Policy();
        policy.setFsn(fsn);
        policy.setGroup(group);
        return policy;
    }

    public static FsnBand getFsnBand(String fsn, String timeFrame) {
        FsnBand fsnBand = new FsnBand();
        fsnBand.setFsn(fsn);
        fsnBand.setSalesBand(2);
        fsnBand.setPvBand(3);
        fsnBand.setTimeFrame(timeFrame);
        return fsnBand;
    }

    public static LastAppSupplier getLastAppSupplier(String fsn, String wh, String supplier, int app) {
        LastAppSupplier lastAppSupplier = new LastAppSupplier();
        lastAppSupplier.setFsn(fsn);
        lastAppSupplier.setWarehouse(wh);
        lastAppSupplier.setLastSupplier(supplier);
        lastAppSupplier.setLastApp(app);
        return lastAppSupplier;
    }

    public static WeeklySale getWeeklySale(String fsn, String wh, int week, int saleQuantity) {
        WeeklySale weeklySale = new WeeklySale();
        weeklySale.setFsn(fsn);
        weeklySale.setWarehouse(wh);
        weeklySale.setWeek(week);
        weeklySale.setSaleQty(saleQuantity);
        return weeklySale;
    }

    public static Requirement getRequirement(String fsn, String wh, String state, boolean enabled,
                                             RequirementSnapshot snapshot, double quantity, String supplier,
                                             int mrp, int app, String currency, int sla,
                                             String comment, String procType) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setState(state);
        requirement.setEnabled(enabled);
        requirement.setWarehouse(wh);
        requirement.setRequirementSnapshot(snapshot);
        requirement.setQuantity(quantity);
        requirement.setSupplier(supplier);
        requirement.setMrp(mrp);
        requirement.setApp(app);
        requirement.setCurrency(currency);
        requirement.setSla(sla);
        requirement.setOverrideComment(comment);
        requirement.setProcType(procType);
        return requirement;
    }

    public static RequirementSnapshot getRequirementSnapshot(String forecast, int inventory, int qoh, int po, int req, int intransit) {
        RequirementSnapshot snapshot = new RequirementSnapshot();
        snapshot.setForecast(forecast);
        snapshot.setInventoryQty(inventory);
        snapshot.setQoh(qoh);
        snapshot.setPendingPoQty(po);
        snapshot.setOpenReqQty(req);
        snapshot.setIwitIntransitQty(intransit);
        return snapshot;
    }

    public static Warehouse getWarehouse(String whCode, String whName) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(whCode);
        warehouse.setName(whName);
        return warehouse;
    }

    /*
   * Initialise mocked zulu response
   * */
    public static RetailProductAttributeResponse getZuluData() {
        RetailProductAttributeResponse retailProductAttributeResponse = new RetailProductAttributeResponse();
        EntityView entityView = new EntityView();
        List<EntityView> entityViews = Lists.newArrayList();
        Map<Object, Object> view = new HashMap<>();
        Map<String, String> analyticalInfo = new HashMap<>();
        Map<Object, Object> supplyChainAttributes = new HashMap<>();
        entityView.setEntityId("fsn");
        supplyChainAttributes.put("procurement_title", "dummy_zulu_title");
        Map<String, String> productAttributes = new HashMap<>();
        analyticalInfo.put("vertical", "dummy_zulu_vertical");
        analyticalInfo.put("category", "dummy_zulu_category");
        analyticalInfo.put("super_category", "dummy_zulu_super_category");
        productAttributes.put("brand", "dummy_zulu_brand");
        productAttributes.put("flipkart_selling_price", String.valueOf(2));
        supplyChainAttributes.put("product_attributes", productAttributes);
        view.put("analytics_info", analyticalInfo);
        view.put("supply_chain", supplyChainAttributes);
        entityView.setView(view);
        entityViews.add(entityView);
        retailProductAttributeResponse.setEntityViews(entityViews);
        return retailProductAttributeResponse;
    }

    /*
    * Initialiase mocked db response for getting product info
    * */
    public static List<ProductInfo> getProductInfo() {
        List<ProductInfo> productInfoList = Lists.newArrayList();
        ProductInfo dbProductInfo = new ProductInfo();
        dbProductInfo.setFsn("fsn");
        dbProductInfo.setVertical("dummy_db_vertical");
        dbProductInfo.setSuperCategory("dummy_db_super_category");
        dbProductInfo.setCategory("dummy_db_category");
        dbProductInfo.setBrand("dummy_db_brand");
        dbProductInfo.setTitle("dummy_db_title");
        dbProductInfo.setFsp(1);
        productInfoList.add(dbProductInfo);

        return productInfoList;
    }


    /*
    * Initialize uploaded file data for proposed state
    * */
    public static List<RequirementDownloadLineItem> getProposedRequirementDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*This one has all fields up-to-date*/
        RequirementDownloadLineItem firstItem = new RequirementDownloadLineItem();

        /*Quantity should be overridden*/
        firstItem.setWarehouseName("dummy_warehouse_1");
        firstItem.setFsn("dummy_fsn");
        firstItem.setRequirementId((long) 1);
        firstItem.setQuantity(100);
        firstItem.setIpcQuantityOverride(20);
        firstItem.setIpcQuantityOverrideReason("test_ipc");
        firstItem.setCdoQuantityOverride(15);
        firstItem.setCdoOverrideReason("test_cdo_quantity");
        firstItem.setCdoPriceOverride(100);
        firstItem.setCdoPriceOverrideReason("test_cdo_price");
        firstItem.setCdoSupplierOverride("new_supplier");
        requirementDownloadLineItems.add(firstItem);

        /*Override should fail for this as comment is missing*/
        RequirementDownloadLineItem secondItem = new RequirementDownloadLineItem();
        secondItem.setWarehouseName("dummy_warehouse_2");
        secondItem.setFsn("dummy_fsn");
        secondItem.setRequirementId((long)2);
        secondItem.setQuantity(100);
        secondItem.setIpcQuantityOverride(20);
        requirementDownloadLineItems.add(secondItem);

        /*Override should fail as warehouse is missing*/
        RequirementDownloadLineItem thirdItem = new RequirementDownloadLineItem();
        thirdItem.setFsn("dummy_fsn_1");
        thirdItem.setRequirementId((long) 3);
        thirdItem.setIpcQuantityOverride(20);
        requirementDownloadLineItems.add(thirdItem);

        /*Quantity should be overridden*/
        RequirementDownloadLineItem fourthItem = new RequirementDownloadLineItem();
        fourthItem.setFsn("dummy_fsn_1");
        fourthItem.setWarehouseName("dummy_warehouse");
        fourthItem.setQuantity(100);
        fourthItem.setRequirementId((long) 4);
        fourthItem.setIpcQuantityOverride(0);
        fourthItem.setIpcQuantityOverrideReason("test_ipc");
        requirementDownloadLineItems.add(fourthItem);

        return requirementDownloadLineItems;
    }

    /*
    * Initialize uploaded file data for Cdo review state
    * */
    public static List<RequirementDownloadLineItem> getCdoReviewRequirementDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*Quantity, app, supplier, sla should be overridden with comment*/
        RequirementDownloadLineItem firstItem = new RequirementDownloadLineItem();
        firstItem.setWarehouseName("dummy_warehouse_1");
        firstItem.setRequirementId((long) 1);
        firstItem.setFsn("fsn");
        firstItem.setCdoQuantityOverride(20);
        firstItem.setCdoQuantityOverrideReason("test_cdo_quantity");
        firstItem.setCdoPriceOverride(100);
        firstItem.setCdoPriceOverrideReason("test_cdo_price");
        firstItem.setCdoSupplierOverride("new_supplier");
        firstItem.setCdoSupplierOverrideReason("test_cdo_supplier");
        firstItem.setNewSla(20);
        requirementDownloadLineItems.add(firstItem);

        /*Quantity override should not happen*/
        RequirementDownloadLineItem secondItem = new RequirementDownloadLineItem();
        secondItem.setRequirementId((long) 2);
        secondItem.setWarehouseName("dummy_warehouse_2");
        secondItem.setFsn("fsn");
        secondItem.setCdoQuantityOverride(-1);
        secondItem.setCdoQuantityOverrideReason("test_cdo_quantity");
        requirementDownloadLineItems.add(secondItem);

        /*No override should happen as comment is missing*/
        RequirementDownloadLineItem thirdItem = new RequirementDownloadLineItem();
        thirdItem.setRequirementId((long) 3);
        thirdItem.setWarehouseName("dummy_warehouse_1");
        thirdItem.setFsn("fsn_1");
        thirdItem.setCdoQuantityOverride(20);
        requirementDownloadLineItems.add(thirdItem);

        /*No override should happen as sla < 0 and supplier override comment is absent*/
        RequirementDownloadLineItem fourthItem = new RequirementDownloadLineItem();
        fourthItem.setFsn("fsn_1");
        fourthItem.setRequirementId((long) 4);
        fourthItem.setWarehouseName("dummy_warehouse_2");
        fourthItem.setNewSla(-1);
        fourthItem.setCdoSupplierOverride("new_supplier");
        requirementDownloadLineItems.add(fourthItem);

        /*No override as app quantity is less than zero and reason is missing*/
        RequirementDownloadLineItem fifthItem = new RequirementDownloadLineItem();
        fifthItem.setFsn("fsn_2");
        fifthItem.setRequirementId((long) 5);
        fifthItem.setWarehouseName("dummy_warehouse_1");
        fifthItem.setCdoPriceOverride(-1);
        fifthItem.setCdoPriceOverrideReason("  ");
        requirementDownloadLineItems.add(fifthItem);

        /*Supplier and sla should be overridden*/
        RequirementDownloadLineItem sixthItem = new RequirementDownloadLineItem();
        sixthItem.setFsn("fsn_2");
        sixthItem.setRequirementId((long) 6);
        sixthItem.setWarehouseName("dummy_warehouse_2");
        sixthItem.setCdoSupplierOverride("new Supplier");
        sixthItem.setCdoSupplierOverrideReason("test_cdo_supplier");
        sixthItem.setNewSla(20);
        requirementDownloadLineItems.add(sixthItem);


        return requirementDownloadLineItems;

    }

    /*
    * Initialize uploaded file data for bizfin review state
    * */
    public static List<RequirementDownloadLineItem> getBizfinReviewDownloadLineItem() {
        List<RequirementDownloadLineItem> requirementDownloadLineItems = new ArrayList<>();

        /*Quantity and comment should be overridden*/
        RequirementDownloadLineItem firstItem = new RequirementDownloadLineItem();
        firstItem.setWarehouseName("dummy_warehouse_1");
        firstItem.setRequirementId((long) 1);
        firstItem.setFsn("fsn");
        firstItem.setBizFinRecommendedQuantity(20);
        firstItem.setBizFinComment("test_bizfin");
        requirementDownloadLineItems.add(firstItem);

        /*Override should fail as comment is missing*/
        RequirementDownloadLineItem secondItem = new RequirementDownloadLineItem();
        secondItem.setWarehouseName("dummy_warehouse_2");
        secondItem.setRequirementId((long) 2);
        secondItem.setFsn("fsn");
        secondItem.setBizFinRecommendedQuantity(20);
        requirementDownloadLineItems.add(secondItem);

        /*Override only the comment*/
        RequirementDownloadLineItem thirdItem = new RequirementDownloadLineItem();
        thirdItem.setFsn("fsn_1");
        thirdItem.setRequirementId((long) 3);
        thirdItem.setWarehouseName("dummy_warehouse_1");
        thirdItem.setBizFinComment("test_bizfin");
        requirementDownloadLineItems.add(thirdItem);

        /*Should impact nothing on upload */
        RequirementDownloadLineItem fourthItem = new RequirementDownloadLineItem();
        fourthItem.setRequirementId((long) 4);
        fourthItem.setFsn("fsn_1");
        fourthItem.setWarehouseName("dummy_warehouse_2");
        requirementDownloadLineItems.add(fourthItem);


        return requirementDownloadLineItems;
    }

    public static Forecast getForecast(String fsn, String warehouse, String forecastString) {
        Forecast forecast = new Forecast();
        forecast.setFsn(fsn);
        forecast.setWarehouse(warehouse);
        forecast.setForecast(forecastString);
        return forecast;
    }

    public static WarehouseSupplierSla getWarehouseSupplierSla(String vertical, String warehouse, String supplier, int sla) {
        WarehouseSupplierSla warehouseSupplierSla = new WarehouseSupplierSla();
        warehouseSupplierSla.setVertical(vertical);
        warehouseSupplierSla.setWarehouseId(warehouse);
        warehouseSupplierSla.setSupplierId(supplier);
        warehouseSupplierSla.setSla(sla);
        return warehouseSupplierSla;
    }
}
