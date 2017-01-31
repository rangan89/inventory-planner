package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 27/01/17.
 */


@Entity
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@Table(name = "REQUIREMENT_SNAPSHOT")
public class RequirementSnapshot extends ReadOnlyEntity {

    private String forecast;

    @Column(name = "inventory_qty")
    private int inventoryQty;

    private int qoh;

    @Column(name = "pending_po_qty")
    private int pendingPOQty;

    @Column(name = "open_req_qty")
    private int openReqQty;

    @Column(name = "iwit_intransit_qty")
    private int iwitIntransitQty;

    private String policy;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Group group;


}
