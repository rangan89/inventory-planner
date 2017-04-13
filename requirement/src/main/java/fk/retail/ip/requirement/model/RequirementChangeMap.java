package fk.retail.ip.requirement.model;

import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class RequirementChangeMap {
    String attribute;
    String oldValue;
    String newValue;
    String reason;
    String eventType;
    String user;
}
