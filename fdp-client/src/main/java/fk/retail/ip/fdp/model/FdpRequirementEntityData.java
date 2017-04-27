package fk.retail.ip.fdp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import java.util.List;
import java.util.Date;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class FdpRequirementEntityData {
    String requirementId;
    String partyId;
    String fsn;
    String warehouse;
    String forecast;
    double quantity;
    String supplier;
    Integer app;
    Integer mrp;
    String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Date requiredByDate;
    Integer inventoryQty;
    Integer pendingPoQty;
    Integer openReqQty;
    Integer iwitIntransitQty;
    String state;
    String poId;
    Boolean enabled;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Date updatedAt;
    List<PolicyValueMap> policies;
}
