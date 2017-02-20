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

    //todo: verify with excel columns

    @JsonProperty("FSN")
    private String fsn;
    @JsonProperty("Warehouse")
    private String warehouse;
    @JsonProperty("PV Band")
    private Integer pvBand;
    @JsonProperty("Sales Band")
    private Integer salesBand;
    @JsonProperty("Sales_bucket_0")
    private Integer week0Sale;
    @JsonProperty("Sales_bucket_1")
    private Integer week1Sale;
    @JsonProperty("Sales_bucket_2")
    private Integer week2Sale;
    @JsonProperty("Sales_bucket_3")
    private Integer week3Sale;
    @JsonProperty("Sales_bucket_4")
    private Integer week4Sale;
    @JsonProperty("Sales_bucket_5")
    private Integer week5Sale;
    @JsonProperty("Sales_bucket_6")
    private Integer week6Sale;
    @JsonProperty("Sales_bucket_7")
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
    @JsonProperty("procurement_type")
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
    @JsonProperty("CDO Override reason")
    private String cdoOverrideReason;

    public RequirementDownloadLineItem(Requirement req) {
        this.fsn = req.getFsn();

        //todo: display warehouse as full name
        this.warehouse = req.getWarehouse();
        this.inventory = req.getRequirementSnapshot().getInventoryQty();
        this.qoh = req.getRequirementSnapshot().getQoh();

        Integer iwitQuantity = req.getRequirementSnapshot().getIwitIntransitQty();
        Integer pendingPOQty = req.getRequirementSnapshot().getPendingPoQty();
        Integer openReqQty = req.getRequirementSnapshot().getOpenReqQty();
        this.intransitQty = (iwitQuantity != null? iwitQuantity : 0);
        this.intransitQty += (pendingPOQty != null? pendingPOQty : 0);
        this.intransitQty += (openReqQty != null? openReqQty : 0);

        this.forecast = req.getRequirementSnapshot().getForecast();
        this.quantity = req.getQuantity()!= null ? req.getQuantity():0;
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp() != null ? req.getApp() :0;
        this.totalValue = this.app * this.quantity;
        this.currency = req.getCurrency();
        this.sla = req.getSla();
        this.procType = req.getProcType();

    }

}
