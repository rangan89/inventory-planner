package fk.retail.ip.requirement.internal.states;

import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */

public interface RequirementState {

    StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired);

}
