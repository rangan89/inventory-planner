package fk.retail.ip.requirement.internal.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "transfer_items")
public class IwtRequestItem extends ReadOnlyEntity {

    String fsn;
    int availableQuantity;
    String status;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "external_id", referencedColumnName = "externalId")
    IwtRequest iwtRequest;

    public String getWarehouse() {
        return iwtRequest.getDestinationWarehouse();
    }
}
