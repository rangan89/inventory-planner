package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "INTRANSIT")
public class OpenRequirementAndPurchaseOrder extends ReadOnlyEntity {
    String fsn;
    String warehouse;
    @Column(name = "pendingPOs")
    int pendingPurchaseOrderQuantity;
    @Column(name = "openRequirements")
    int openRequirementQuantity;
}
