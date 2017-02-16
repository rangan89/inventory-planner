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

    private String fsn;
    private String warehouse;
    private int pvBand;
    private int salesBand;
    @JsonProperty("salesBucket-0")
    private int week0Sale;
    @JsonProperty("salesBucket-1")
    private int week1Sale;
    @JsonProperty("salesBucket-2")
    private int week2Sale;
    @JsonProperty("salesBucket-3")
    private int week3Sale;
    @JsonProperty("salesBucket-4")
    private int week4Sale;
    @JsonProperty("salesBucket-5")
    private int week5Sale;
    @JsonProperty("salesBucket-6")
    private int week6Sale;
    @JsonProperty("salesBucket-7")
    private int week7Sale;
    private int inventory;
    private int qoh;
    private String forecast;
    private int pendingPOQty;
    private int openReqQty;
    private int iwitIntransitQty;
    private int quantity;
    private String supplier;
    private int mrp;
    private int app;
    private String currency;
    private int sla;
    private boolean international;
    private String procType;
    private String overrideComment;

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
