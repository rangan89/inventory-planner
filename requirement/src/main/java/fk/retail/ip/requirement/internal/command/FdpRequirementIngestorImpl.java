package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.fdp.internal.command.FdpClientIngestor;
import fk.retail.ip.fdp.model.*;
import fk.retail.ip.requirement.model.*;
import fk.retail.ip.requirement.internal.entities.Requirement;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yogeshwari.k on 22/03/17.
 */
@Slf4j
public class FdpRequirementIngestorImpl {

    private final FdpClientIngestor fdpClientIngestor;
    private final RequirementToFdpEntityMapper requirementToFdpEntityMapper;
    private final RequirementToFdpEventMapper requirementToFdpEventMapper;

    @Inject
    FdpRequirementIngestorImpl(FdpClientIngestor fdpClientIngestor, RequirementToFdpEntityMapper requirementToFdpEntityMapper, RequirementToFdpEventMapper requirementToFdpEventMapper) {
        this.fdpClientIngestor = fdpClientIngestor;
        this.requirementToFdpEntityMapper = requirementToFdpEntityMapper;
        this.requirementToFdpEventMapper = requirementToFdpEventMapper;
    }

    public void pushToFdp(List<RequirementChangeRequest> requirementChangeRequests) {
        BatchFdpRequirementEventEntityPayload batchFdpRequirementEventEntityPayload = new BatchFdpRequirementEventEntityPayload();
        requirementChangeRequests.forEach(req -> {
            String requirementId= getRequirementId(req.getRequirement());
            FdpEntityPayload<FdpRequirementEntityData> fdpRequirementEntityPayload = requirementToFdpEntityMapper.convertToEntityPayload(requirementId,req.getRequirement());
            List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayload = requirementToFdpEventMapper.convertToEventPayload(requirementId,req.getRequirementChangeMaps());
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().add(fdpRequirementEntityPayload);
            batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().addAll(fdpRequirementEventPayload);
        });

        log.info("Pushing {} number of requirement entities to fdp", batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().size());
        log.info("Pushing {} number of requirement events to fdp", batchFdpRequirementEventEntityPayload.getPurchaseRequirementEvent().size());
        if(!batchFdpRequirementEventEntityPayload.getPurchaseRequirementEntity().isEmpty())
            fdpClientIngestor.pushToFdp(batchFdpRequirementEventEntityPayload);
    }

    private String getRequirementId(Requirement requirement) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String requirementId = requirement.getFsn()+"_"+requirement.getWarehouse()+"_"+(sdf.format(requirement.getCreatedAt()).toString());
        return requirementId;
    }

}
