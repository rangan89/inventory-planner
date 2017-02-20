package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fk.retail.ip.requirement.internal.entities.Requirement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@XmlRootElement
@Getter
@Setter
public class RequirementDownloadLineItem {

    @JsonProperty("FSN")
    private String fsn;
    @JsonProperty("Warehouse")
    private String warehouse;
    @JsonProperty("PV Band")
    private int pvBand;
    @JsonProperty("Sales Band")
    private int salesBand;
    @JsonProperty("Sales_bucket_0")
    private int week0Sale;
    @JsonProperty("Sales_bucket_1")
    private int week1Sale;
    @JsonProperty("Sales_bucket_2")
    private int week2Sale;
    @JsonProperty("Sales_bucket_3")
    private int week3Sale;
    @JsonProperty("Sales_bucket_4")
    private int week4Sale;
    @JsonProperty("Sales_bucket_5")
    private int week5Sale;
    @JsonProperty("Sales_bucket_6")
    private int week6Sale;
    @JsonProperty("Sales_bucket_7")
    private int week7Sale;
    @JsonProperty("Inventory")
    private int inventory;
    private int qoh;
    @JsonProperty("Forecast")
    private String forecast;
    private int pendingPOQty;
    private int openReqQty;
    @JsonProperty("Intransit")
    private int iwitIntransitQty;
    @JsonProperty("Quantity")
    private int quantity;
    @JsonProperty("Supplier")
    private String supplier;
    @JsonProperty("MRP")
    private int mrp;
    @JsonProperty("Purchase Price")
    private int app;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("SLA")
    private int sla;
    private boolean international;
    @JsonProperty("procurement_type")
    private String procType;
    private String overrideComment;
    @JsonProperty("Last App")
    private Integer lastApp;
    @JsonProperty("Last Supplier")
    private String lastSupplier;
    @JsonProperty("BizFin Quantity Recommendation")
    private int bizFinRecommendedQuantity;
    @JsonProperty("BizFin Comments")
    private String bizFinComment;
    @JsonProperty("IPC Proposed Quantity")
    private int ipcProposedQuantity;
    @JsonProperty("CDO Override reason")
    private String cdoOverrideReason;

    public RequirementDownloadLineItem(Requirement req) {
        this.fsn = req.getFsn();
        this.warehouse = req.getWarehouse();
        this.inventory = req.getRequirementSnapshot().getInventoryQty();
        this.qoh = req.getRequirementSnapshot().getQoh();
        this.pendingPOQty = req.getRequirementSnapshot().getPendingPoQty();
        this.iwitIntransitQty = req.getRequirementSnapshot().getIwitIntransitQty();
        this.openReqQty = req.getRequirementSnapshot().getOpenReqQty();
        this.forecast = req.getRequirementSnapshot().getForecast();
        this.quantity = req.getQuantity();
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp();
        this.currency = req.getCurrency();
        this.sla = req.getSla();
        this.international = req.isInternational();
        this.procType = req.getProcType();
        this.overrideComment = req.getOverrideComment();

    }

}
