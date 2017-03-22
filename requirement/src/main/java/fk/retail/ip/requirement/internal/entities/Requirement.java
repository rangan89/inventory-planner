package fk.retail.ip.requirement.internal.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 26/01/17.
 */

@Entity
@XmlRootElement
@Data
@NoArgsConstructor
//todo: cleanup
@Table(name = "projection_states")
//@Table(name = "REQUIREMENT")
public class Requirement extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    //todo : add this field in projection_states in old db
    @NotNull
    private String fsn;

    @NotNull
    private String warehouse;

    //todo:cleanup
    @Column(name = "qty")
    private double quantity;

    private String supplier;

    private Integer mrp;

    private Integer app;

    //todo:cleanup
    @Column(name = "app_currency")
    private String currency;

    private Integer sla;

    private boolean international;

    @NotNull
    private String state;

    private String procType;


    //todo:cleanup
    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "is_current")
    private boolean current;

    //todo: cleanup
    @Column(name = "comment")
    private String overrideComment;

    private String createdBy;

    private Long sslId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requirement_snapshot_id")
    private RequirementSnapshot requirementSnapshot;

    //todo: cleanup (fields for backward compatibilty)
    //TODO: legacy code
    @Column(name = "prev_state_id")
    private Long previousStateId;

    //TODO: legacy code
    @Column(name = "pan_india")
    private Integer panIndiaQuantity;

    //TODO: legacy code
    @Column(name = "projection_id")
    private Long projectionId;

    //todo:cleanup
    private String mrpCurrency;

    public Requirement(Long id) {
        this.id = id;
    }

    public Requirement(Requirement other) {
        fsn = other.fsn;
        warehouse = other.warehouse;
        quantity = other.quantity;
        supplier = other.supplier;
        mrp = other.mrp;
        mrpCurrency = other.mrpCurrency;
        app = other.app;
        currency = other.currency;
        sla = other.sla;
        international = other.international;
        procType = other.procType;
        enabled = other.enabled;
        current = other.current;
        requirementSnapshot = other.requirementSnapshot;

        //TODO: legacy code
        projectionId = other.projectionId;
        panIndiaQuantity = other.panIndiaQuantity;
        sslId = other.sslId;
    }
}
