package fk.retail.ip.fdp.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class FdpRequirementEventData {
    String user;
    String requirementId;
    String attribute;
    String oldValue;
    String newValue;
    String reason;
    String eventType;
}
