package fk.retail.ip.requirement.model;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.fdp.model.FdpRequirementEventData;
import fk.retail.ip.fdp.model.FdpEventPayload;
import java.util.List;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
public class RequirementToFdpEventMapper {

    private final FdpConfiguration fdpConfiguration;

    @Inject
    public RequirementToFdpEventMapper(FdpConfiguration fdpConfiguration){
        this.fdpConfiguration = fdpConfiguration;
    }

    public List<FdpEventPayload<FdpRequirementEventData>> convertToEventPayload(Object requirementId, List<RequirementChangeMap> requirementChangeMaps) {
        List<FdpEventPayload<FdpRequirementEventData>> fdpRequirementEventPayloadList = Lists.newArrayList();
        requirementChangeMaps.forEach(changeMap -> {
            FdpEventPayload fdpRequirementEventPayload =new FdpEventPayload();
            fdpRequirementEventPayload.setEventId(getEventId(requirementId.toString()));
            fdpRequirementEventPayload.setSchemaVersion(fdpConfiguration.getRequirementEventSchemaVersion());
            fdpRequirementEventPayload.setEventTime(new Date());
            fdpRequirementEventPayload.setData(getRequirementEventData(requirementId.toString(),changeMap));
            fdpRequirementEventPayloadList.add(fdpRequirementEventPayload);
        });
        return fdpRequirementEventPayloadList;
    }

    private String getEventId(String requirementId) {
        return requirementId+UUID.randomUUID();
    }

    private FdpRequirementEventData getRequirementEventData(String requirementId, RequirementChangeMap requirementChangeMap){
        FdpRequirementEventData fdpRequirementEventData = new FdpRequirementEventData();
        fdpRequirementEventData.setRequirementId(requirementId);
        fdpRequirementEventData.setAttribute(requirementChangeMap.getAttribute());
        fdpRequirementEventData.setOldValue(requirementChangeMap.getOldValue());
        fdpRequirementEventData.setNewValue(requirementChangeMap.getNewValue());
        fdpRequirementEventData.setReason(requirementChangeMap.getReason());
        fdpRequirementEventData.setUser(requirementChangeMap.getUser());
        fdpRequirementEventData.setEventType(requirementChangeMap.getEventType());
        return fdpRequirementEventData;
    }
}
