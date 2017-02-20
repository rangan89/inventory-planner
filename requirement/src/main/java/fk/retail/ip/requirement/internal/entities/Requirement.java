package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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
    @NotNull
    private Integer quantity;

    private String supplier;

    private Integer mrp;

    private Integer app;

    //todo:cleanup
    @Column(name = "app_currency")
    private String currency;

    private Integer sla;

    @NotNull
    private String state;

    private String procType;


    //todo:cleanup
    @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "enabled", columnDefinition = "TINYINT")
    //@Column(name = "is_enabled")
    private Boolean enabled;

    @Column(name = "is_current")
    private Boolean current;

    //todo: cleanup
    @Column(name = "comment")
    private String overrideComment;

    private String createdBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_snapshot_id")
    private RequirementSnapshot requirementSnapshot;


    //todo: cleanup (fields for backward compatibilty)
    private Long projectionId;
    private Integer panIndia;
    private String mrpCurrency;
    private String sslId;
    private Long prevStateId;
    private Integer international;

}
