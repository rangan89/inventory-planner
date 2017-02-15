package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "externalId", referencedColumnName = "externalId")
    IwtRequest iwtRequest;

    public String getWarehouse() {
        return iwtRequest.getDestinationWarehouse();
    }
}
