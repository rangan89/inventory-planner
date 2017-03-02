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
    int dirty = 0;
    int enabled;
    int intransit;
    int inventory;
    String sku = "N/A";
    String procType;
    long forecastId;
    String policyId;
    long groupId;
    String error;
}
