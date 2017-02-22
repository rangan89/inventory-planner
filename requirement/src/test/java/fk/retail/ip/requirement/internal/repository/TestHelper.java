package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import fk.retail.ip.requirement.internal.entities.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fk.retail.ip.zulu.internal.entities.EntityView;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import org.bouncycastle.cert.ocsp.Req;

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

    public static Group getGroup(String name) {
        Group group = new Group();
        group.setName(name);
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

    public static LastAppSupplier getLastAppSupplier(String fsn, String wh,String supplier, int app ) {
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
                                             RequirementSnapshot snapshot, int quantity, String supplier,
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

    public static RequirementSnapshot getRequirementSnapshot(String forecast, int inventory, int qoh, int po , int req, int intransit) {
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
        warehouse.setWarehouseCode(whCode);
        warehouse.setWarehouseName(whName);
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

}
