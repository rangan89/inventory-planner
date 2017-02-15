package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "iwt_requests")
public class IwtRequest extends ReadOnlyEntity {

    String externalId;
    @Column(name = "dest_wh")
    String destinationWarehouse;
    String status;
}
