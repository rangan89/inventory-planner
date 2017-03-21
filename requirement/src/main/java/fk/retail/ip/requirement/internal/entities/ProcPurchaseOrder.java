package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProcPurchaseOrder {
    @Id
    long id;
    String fsn;
    String vertical;
}
