package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "projections")
@Deprecated
public class Projection extends AbstractEntity{

    String fsn;
    String currentState;
    Integer dirty = 0;
    Integer enabled;
    Integer intransit;
    Integer inventory;
    String sku = "N/A";
    String procType;
    Long forecastId;
    String policyId;
    Long groupId;
    String error;
}
