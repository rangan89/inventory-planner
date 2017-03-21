package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class WarehouseSupplierSla {
    @Id
    @GeneratedValue
    long id;
    String vertical;
    String warehouseId;
    String supplierId;
    int sla;
}
