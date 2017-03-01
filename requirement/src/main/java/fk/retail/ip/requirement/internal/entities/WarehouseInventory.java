package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "wh_inventory")
public class WarehouseInventory extends ReadOnlyEntity {

    String fsn;
    String warehouse;
    int quantity;
    int qoh;
}
