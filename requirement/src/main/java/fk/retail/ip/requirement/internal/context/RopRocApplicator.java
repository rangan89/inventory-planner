package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.PayloadCreationHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Map;

import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RopRocApplicator extends PolicyApplicator {

    public RopRocApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext, List<RequirementChangeRequest> requirementChangeRequestList) {
        Map<String, Double> warehouseToRopMap = parseRopRoc(policyTypeMap.get(PolicyType.ROP));
        Map<String, Double> warehouseToRocMap = parseRopRoc(policyTypeMap.get(PolicyType.ROC));
        requirements.stream().filter(requirement -> !RequirementApprovalState.ERROR.toString().equals(requirement.getState())).forEach(requirement -> {
            String warehouse = requirement.getWarehouse();
            Double ropDays = warehouseToRopMap.get(warehouse);
            if (!isValidRopRoc(ropDays)) {
                //rop policy not found
                markAsError(requirement, String.format(Constants.VALID_POLICY_NOT_FOUND, PolicyType.ROP));
                return;
            }
            addToSnapshot(requirement, PolicyType.ROP, ropDays);
            List<Double> forecast = forecastContext.getForecast(fsn, warehouse);
            double ropQuantity = convertDaysToQuantity(ropDays, forecast);
            double onHandQuantity = onHandQuantityContext.getTotalQuantity(fsn, warehouse);
            if (onHandQuantity <= ropQuantity) {
                //reorder point has been reached
                Double rocDays = warehouseToRocMap.get(warehouse);
                if (!isValidRopRoc(rocDays) || rocDays < ropDays) {
                    //roc policy not found
                    markAsError(requirement, String.format(Constants.VALID_POLICY_NOT_FOUND, PolicyType.ROC));
                    return;
                }
                addToSnapshot(requirement, PolicyType.ROC, rocDays);
                double demand = convertDaysToQuantity(rocDays, forecast);
                requirement.setQuantity(demand - onHandQuantity);
                //Add ORDER_POLICY_QUANTITY events to fdp request
                log.info("Adding ORDER_POLICY_QUANTITY events to fdp request");
                RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.QUANTITY.toString(), null, String.valueOf(requirement.getQuantity()), FdpRequirementEventType.ORDER_POLICY_QUANTITY.toString(), "ROP ROC policies applied", "system"));
                requirementChangeRequest.setRequirement(requirement);
                requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                requirementChangeRequestList.add(requirementChangeRequest);
            }
        });
    }

    private Map<String, Double> parseRopRoc(String value) {
        Map<String, Double> policyMap = Maps.newHashMap();
        TypeReference<Map<String, Map<String, Double>>> typeReference = new TypeReference<Map<String, Map<String, Double>>>() {};
        Map<String, Map<String, Double>> rawMap = super.parsePolicy(value, typeReference);
        if (rawMap != null) {
            rawMap.entrySet().stream().forEach(entry -> policyMap.put(entry.getKey(), entry.getValue().get("days")));
        }
        return policyMap;
    }

    public boolean isValidRopRoc(Double value) {
        if (value == null) {
            return false;
        } else if (value < 0 || value > Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK) {
            return false;
        } else {
            return true;
        }
    }
}
