package fk.retail.ip.requirement.internal.context;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Map;
import lombok.Data;

public class OnHandQuantityContext {
    private Table<String, String, Quantity> fsnWarehouseQuantityTable = HashBasedTable.create();


    public Quantity addInventoryQuantity(String fsn, String warehouse, int inventory, int qoh) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity == null) {
            quantity = new Quantity();
        }
        quantity.setInventoryAvailableToPromise(inventory);
        quantity.setQoh(qoh);
        return fsnWarehouseQuantityTable.put(fsn, warehouse, quantity);
    }

    public Quantity addOpenRequirementAndPurchaseOrder(String fsn, String warehouse, int openRequirement, int pendingPurchaseOrder) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity == null) {
            quantity = new Quantity();
        }
        quantity.setOpenRequirement(openRequirement);
        quantity.setPendingPurchaseOrder(pendingPurchaseOrder);
        return fsnWarehouseQuantityTable.put(fsn, warehouse, quantity);
    }

    public Quantity addIwtQuantity(String fsn, String warehouse, int intransit) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity == null) {
            quantity = new Quantity();
        }
        quantity.iwtIntransit += intransit;
        return fsnWarehouseQuantityTable.put(fsn, warehouse, quantity);
    }

    public double getInventoryQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return quantity.getInventoryAvailableToPromise();
        } else {
            return 0;
        }
    }

    public double getOnHandInventoryQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return quantity.getQoh();
        } else {
            return 0;
        }
    }

    public double getOpenRequirementQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return fsnWarehouseQuantityTable.get(fsn, warehouse).getOpenRequirement();
        } else {
            return 0;
        }
    }

    public double getPendingPurchaseOrderQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return fsnWarehouseQuantityTable.get(fsn, warehouse).getPendingPurchaseOrder();
        } else {
            return 0;
        }
    }

    public double getIwtQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return fsnWarehouseQuantityTable.get(fsn, warehouse).getIwtIntransit();
        } else {
            return 0;
        }
    }

    public double getTotalQuantity(String fsn, String warehouse) {
        Quantity quantity = fsnWarehouseQuantityTable.get(fsn, warehouse);
        if (quantity != null) {
            return fsnWarehouseQuantityTable.get(fsn, warehouse).getTotal();
        } else {
            return 0;
        }
    }

    public double getTotalQuantity(String fsn) {
        Map<String, Quantity> warehouseToQuantityMap = fsnWarehouseQuantityTable.row(fsn);
        double total = warehouseToQuantityMap.values().stream().mapToDouble(Quantity::getTotal).sum();
        return total;
    }

    @Data
    public static class Quantity {
        double inventoryAvailableToPromise;
        double iwtIntransit;
        double openRequirement;
        double pendingPurchaseOrder;
        double qoh;
        
        public double getTotal() {
            return inventoryAvailableToPromise + iwtIntransit + openRequirement + pendingPurchaseOrder;
        }
    }
}
