package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.IwtRequest;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import java.util.Date;
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

}
