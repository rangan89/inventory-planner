package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.Data;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@Data
@Getter
public class RequirementSearchLineItem {

    private String fsn;
    private String warehouse;
    private String vertical;
    private String category;
    private String superCategory;
    private String title;
    private String brand;
    private Integer fsp;
    private Integer pvBand;
    private Integer salesBand;
    private Integer week0Sale;
    private Integer week1Sale;
    private Integer week2Sale;
    private Integer week3Sale;
    private Integer week4Sale;
    private Integer week5Sale;
    private Integer week6Sale;
    private Integer week7Sale;
    private Integer inventory;
    private Integer qoh;
    private String forecast;
    private Integer totalValue;
    private Integer intransitQty;
    private Integer qty;
    private String supplier;
    private Integer mrp;
    private Integer app;
    private String appCurrency;
    private Integer sla;
    private String procType;
    private Long requirementId;
    private String createdBy;
    private Integer international;
    private String mrpCurrency;
    private boolean enabled;
    private String state;
    private boolean isCurrent;
    private Long projectionId;
    private Long groupId;

    public RequirementSearchLineItem(Requirement req) {

        this.requirementId = req.getId();
        this.fsn = req.getFsn();

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
            this.groupId = req.getRequirementSnapshot().getGroup().getId();
        }
        this.qty = (int) req.getQuantity();
        this.supplier = req.getSupplier();
        this.mrp = req.getMrp();
        this.app = req.getApp() != null ? req.getApp() : 0;
        this.appCurrency = req.getCurrency();
        this.totalValue = this.app * this.qty;
        this.sla = req.getSla();
        this.procType = req.getProcType();
        this.createdBy = req.getCreatedBy();
        this.international = req.isInternational() ? 1 : 0;
        this.mrpCurrency = req.getMrpCurrency();
        this.enabled = req.isEnabled();
        this.state = req.getState();
        this.isCurrent = req.isCurrent();
        this.projectionId = req.getProjectionId();
    }
}
