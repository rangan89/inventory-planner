package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.requirement.model.RequirementChangeMap;

/**
 * Created by yogeshwari.k on 24/03/17.
 */
public class PayloadCreationHelper {

    public static RequirementChangeMap createChangeMap(String attribute, String oldValue, String newValue, String eventType, String reason, String user){
        RequirementChangeMap requirementChangeMap = new RequirementChangeMap();
        requirementChangeMap.setAttribute(attribute);
        requirementChangeMap.setOldValue(oldValue);
        requirementChangeMap.setNewValue(newValue);
        requirementChangeMap.setEventType(eventType);
        requirementChangeMap.setReason(reason);
        requirementChangeMap.setUser(user);
        return requirementChangeMap;
    }

}
