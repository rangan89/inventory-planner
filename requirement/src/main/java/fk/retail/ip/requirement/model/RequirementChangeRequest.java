package fk.retail.ip.requirement.model;

import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.Data;
import java.util.List;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class RequirementChangeRequest {
    Requirement requirement;
    List<RequirementChangeMap> requirementChangeMaps;
}
