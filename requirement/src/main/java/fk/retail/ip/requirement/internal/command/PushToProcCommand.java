package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.proc.internal.command.PushToProcClient;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.proc.model.PushToProcRequest;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yogeshwari.k on 07/04/17.
 */
@Slf4j
public class PushToProcCommand {

    private final RequirementRepository requirementRepository;
    private final PushToProcClient pushToProcClient;
    private final FdpRequirementIngestorImpl fdpRequirementIngestor;

    @Inject
    PushToProcCommand(RequirementRepository requirementRepository, PushToProcClient pushToProcClient, FdpRequirementIngestorImpl fdpRequirementIngestor) {
        this.requirementRepository = requirementRepository;
        this.pushToProcClient = pushToProcClient;
        this.fdpRequirementIngestor = fdpRequirementIngestor;
    }

    private Map<Long, PushToProcRequest> getPushToProcRequest(List<Requirement> requirements) {
        return requirements.stream()
                .collect(Collectors.toMap(requirement -> requirement.getId(), requirement -> PushToProcRequest.builder()
                        .fsn(requirement.getFsn())
                        .sku("SKU0000000000000")
                        .quantity((int)requirement.getQuantity())
                        .local(true)
                        .supplierApp(getApp(requirement))
                        .supplierMrp(getMrp(requirement))
                        .sourceId(requirement.getSupplier())
                        .requiredByDate(getRequiredByDate(requirement))
                        .requirementType(requirement.getProcType())
                        .currency(requirement.getCurrency())
                        .warehouseId(requirement.getWarehouse())
                        .build()));
    }

    private Float getApp(Requirement requirement) {
        if(requirement.getApp()!=null)
            return (float)requirement.getApp();
        return null;
    }

    private Float getMrp(Requirement requirement) {
        if(requirement.getMrp()!=null)
            return (float)requirement.getMrp();
        return null;
    }

    private Date getRequiredByDate(Requirement requirement) {
        // set required by date based on holiday calendar
        DateTime dt = new DateTime();
        if(requirement.getSla()!=null)
            dt.plusDays(requirement.getSla());
        DateTime adjustedDate = dt;
        if (dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            adjustedDate = dt.plusDays(2);
        }
        if (dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            adjustedDate = dt.plusDays(1);
        }
        return adjustedDate.toDate();
    }

    private List<Requirement> createPushToProcRequirement(List<Requirement> requirements, String userId) {
        List<RequirementChangeRequest> requirementChangeRequestList = Lists.newArrayList();
        Map<Long, String> groupToTargetState = new HashMap<>();
        groupToTargetState.put((long) 1,RequirementApprovalState.PUSHED_TO_PROC.toString());
        log.info("Constructed map for group to Target state " + groupToTargetState);
        List<Requirement> pushToProcRequirements = Lists.newArrayList();
        requirements.forEach(requirement -> {
            Requirement newEntity = new Requirement(requirement);
            requirement.setCurrent(false);
            newEntity.setCreatedBy(userId);
            newEntity.setPreviousStateId(requirement.getId());
            newEntity.setCurrent(true);
            String eventType;
            String reason;
            if(requirement.getQuantity()==0 || StringUtils.isBlank(requirement.getSupplier()) || "-".equals(requirement.getSupplier())) {
                newEntity.setState(RequirementApprovalState.ERROR.toString());
                newEntity.setOverrideComment(Constants.PUSHED_TO_PROC_FAILED.toString());
                eventType = FdpRequirementEventType.PUSH_TO_PROC_FAILED.toString();
                reason = "push to proc failed";
            }
            else {
                newEntity.setState(RequirementApprovalState.PUSHED_TO_PROC.toString());
                pushToProcRequirements.add(newEntity);
                eventType = FdpRequirementEventType.PUSHED_TO_PROC.toString();
                reason = "pushed to proc";
            }
            requirementRepository.persist(newEntity);
            pushToFdp(requirementChangeRequestList, requirement, newEntity, reason, eventType, userId);
        });
        log.info("Updating Projections tables for Requirements");
        requirementRepository.updateProjections(requirements, groupToTargetState);
        //Push PUSHED_TO_PROC, PUSH_TO_PROC_FAILED events to fdp
        log.info("Pushing PUSHED_TO_PROC, PUSH_TO_PROC_FAILED events to fdp");
        fdpRequirementIngestor.pushToFdp(requirementChangeRequestList);
        return pushToProcRequirements;
    }

    public void pushToFdp(List<RequirementChangeRequest> requirementChangeRequestList, Requirement requirement, Requirement newEntity, String reason, String eventType, String userId) {
        //Add PUSHED_TO_PROC, PUSH_TO_PROC_FAILED events to fdp request
        RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
        List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
        log.info("Adding PUSHED_TO_PROC, PUSH_TO_PROC_FAILED events to fdp request");
        requirementChangeRequest.setRequirement(newEntity);
        requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.STATE.toString(), requirement.getState(), newEntity.getState(), eventType, reason, userId));
        requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
        requirementChangeRequestList.add(requirementChangeRequest);
    }

    public int pushToProc(List<Requirement> requirements, String userId) {
        List<Requirement> pushToProcRequirements = createPushToProcRequirement(requirements,userId);
        Map<Long, PushToProcRequest> allRequirements = getPushToProcRequest(pushToProcRequirements);
        pushToProcClient.pushToProc(allRequirements);
        return allRequirements.size();
    }
}
