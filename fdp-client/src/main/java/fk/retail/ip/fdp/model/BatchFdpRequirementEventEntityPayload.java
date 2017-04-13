package fk.retail.ip.fdp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import com.google.common.collect.Lists;
/**
 * Created by yogeshwari.k on 16/03/17.
 */
@Data
public class BatchFdpRequirementEventEntityPayload implements FdpPayload {
    @JsonProperty("PurchaseRequirementEntity")
    List<FdpEntityPayload<FdpRequirementEntityData>> purchaseRequirementEntity = Lists.newArrayList();
    @JsonProperty("PurchaseRequirementEvent")
    List<FdpEventPayload<FdpRequirementEventData>> purchaseRequirementEvent = Lists.newArrayList();
}
