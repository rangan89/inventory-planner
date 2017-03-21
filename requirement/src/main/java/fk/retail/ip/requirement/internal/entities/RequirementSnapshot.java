package fk.retail.ip.requirement.internal.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
@Entity
@XmlRootElement
@Data
@Table(name = "requirement_snapshot")
public class RequirementSnapshot extends ReadOnlyEntity {

    private String forecast;

    private Integer inventoryQty;

    private Integer qoh;

    private Integer pendingPoQty;

    private Integer openReqQty;

    private Integer iwitIntransitQty;

    private String policy;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_id")
    private Group group;

}
