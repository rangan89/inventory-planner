package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Created by yogeshwari.k on 21/02/17.
 */

@Entity
@Table(name = "INVENTORY_PLAN_WAREHOUSES")
@XmlRootElement
@Data

public class Warehouse extends ReadOnlyEntity{

    @Column(name = "whName")
    private String warehouseCode;

    @Column(name = "name")
    private String warehouseName;
}
