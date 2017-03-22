package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fk.retail.ip.requirement.internal.entities.Requirement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@XmlRootElement
@Data
@NoArgsConstructor
public class RequirementDownloadLineItem {

    //todo: verify with excel columns
    @JsonProperty("FSN")
    private String fsn;
    private String warehouse;
    @JsonProperty("Vertical")
    private String vertical;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("Super Category")
    private String superCategory;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Brand")
    private String brand;
    @JsonProperty("Flipkart Selling Price")
    private Integer fsp;
    @JsonProperty("PV Band")
    private Integer pvBand;
    @JsonProperty("Sales Band")
    private Integer salesBand;
    @JsonProperty("Sales bucket-0")
    private Integer week0Sale;
    @JsonProperty("Sales bucket-1")
    private Integer week1Sale;
    @JsonProperty("Sales bucket-2")
    private Integer week2Sale;
    @JsonProperty("Sales bucket-3")
    private Integer week3Sale;
    @JsonProperty("Sales bucket-4")
    private Integer week4Sale;
    @JsonProperty("Sales bucket-5")
    private Integer week5Sale;
    @JsonProperty("Sales bucket-6")
    private Integer week6Sale;
    @JsonProperty("Sales bucket-7")
    private Integer week7Sale;
    @JsonProperty("Inventory")
    private Integer inventory;
    @JsonProperty("QOH")
    private Integer qoh;
    @JsonProperty("Forecast")
    private String forecast;
    @JsonProperty("Total Value")
    private Integer totalValue;
    @JsonProperty("Intransit")
    private Integer intransitQty;
    @JsonProperty("Quantity")
    private Integer quantity;
    @JsonProperty("Supplier")
    private String supplier;
    @JsonProperty("MRP")
    private Integer mrp;
    @JsonProperty("Purchase Price")
    private Integer app;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("SLA")
    private Integer sla;
    @JsonProperty("Procurement Type")
    private String procType;
    @JsonProperty("Last App")
    private Integer lastApp;
    @JsonProperty("Last Supplier")
    private String lastSupplier;
    @JsonProperty("BizFin Quantity Recommendation")
    private Integer bizFinRecommendedQuantity;
    @JsonProperty("BizFin Comments")
    private String bizFinComment;
    @JsonProperty("IPC Proposed Quantity")
    private Integer ipcProposedQuantity;
    @JsonProperty("CDO Override Reason")
    private String cdoOverrideReason;
    @JsonProperty("Warehouse")
    private String warehouseName;
    @JsonProperty("Requirement Id")
    private Long requirementId;
    @JsonProperty("IPC Quantity Override")
    private Integer ipcQuantityOverride;
    @JsonProperty("IPC Quantity Override Reason")
    private String ipcQuantityOverrideReason;
    @JsonProperty ("CDO Quantity Override")
    private Integer cdoQuantityOverride;
    @JsonProperty ("CDO Quantity Override Reason")
    private String cdoQuantityOverrideReason;
    @JsonProperty ("CDO Price Override")
    private Integer cdoPriceOverride;
    @JsonProperty ("CDO Price Override Reason")
    private String cdoPriceOverrideReason;
    @JsonProperty ("New SLA")
    private Integer newSla;
    @JsonProperty ("CDO Supplier Override")
    private String cdoSupplierOverride;
    @JsonProperty("CDO Supplier Override Reason")
    private String cdoSupplierOverrideReason;

    public RequirementDownloadLineItem(Requirement req) {

        this.requirementId = req.getId();
        this.fsn = req.getFsn();

        //todo: display warehouse as full name
        this.warehouse = req.getWarehouse();
        if (req.getRequirementSnapshot() != null) {
            this.inventory = req.getRequirementSnapshot().getInventoryQty();
            this.qoh = req.getRequirementSnapshot().getQoh();
            Integer iwitQuantity = req.getRequirementSnapshot().getIwitIntransitQty();
            Integer pendingPOQty = req.getRequirementSnapshot().getPendingPoQty();
            Integer openReqQty = req.getRequirementSnapshot().getOpenReqQty();
            this.intransitQty = (iwitQuantity != null ? iwitQuantity : 0);
            this.intransitQty += (pendingPOQty != null ? pendingPOQty : 0);
            this.intransitQty += (openReqQty != null ? openReqQty : 0);
            this.forecast = req.getRequirementSnapshot().getForecast();
        }
        this.quantity = (int) req.getQuantity();
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp() != null ? req.getApp() : 0;
        this.totalValue = this.app * this.quantity;
        this.currency = req.getCurrency();
        this.sla = req.getSla();
        this.procType = req.getProcType();
    }
}
