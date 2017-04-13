package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.command.PayloadCreationHelper;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Map;

import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaseSizeApplicator extends PolicyApplicator {

    public CaseSizeApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext, List<RequirementChangeRequest> requirementChangeRequestList) {
        Double maxCoverageDays = parsePolicy(policyTypeMap.get(PolicyType.MAX_COVERAGE), Constants.MAX_COVERAGE_KEY);
        Double caseSize = parsePolicy(policyTypeMap.get(PolicyType.CASE_SIZE), Constants.CASE_SIZE_KEY);
        if (isValidCaseSize(caseSize)) {
            if (MaxCoverageApplicator.isValidMaxCoverage(maxCoverageDays)) {
                //max coverage is present, round everything down
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.CASE_SIZE, caseSize);
                    double roundedQuantity = Math.floor(requirement.getQuantity() / caseSize) * caseSize;
                    double oldQuantity = requirement.getQuantity();
                    requirement.setQuantity(roundedQuantity);
                    //Add CONTROL_POLICY_QUANTITY_OVERRIDE events to fdp request
                    log.info("Adding CONTROL_POLICY_QUANTITY_OVERRIDE(CaseSize) events to fdp request");
                    createRequirementChangeRequest(oldQuantity, requirement, requirementChangeRequestList);
                });
            } else {
                //round to nearest multiple of case size
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.CASE_SIZE, caseSize);
                    double roundedQuantity = Math.round(requirement.getQuantity() / caseSize) * caseSize;
                    double oldQuantity = requirement.getQuantity();
                    requirement.setQuantity(roundedQuantity);
                    //Add CONTROL_POLICY_QUANTITY_OVERRIDE events to fdp request
                    createRequirementChangeRequest(oldQuantity, requirement, requirementChangeRequestList);
                });
            }
        }
    }

    private void createRequirementChangeRequest(double oldQuantity, Requirement requirement, List<RequirementChangeRequest> requirementChangeRequestList) {
        RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
        List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
        if(!Constants.ERROR_STATE.toString().equals(requirement.getState())) {
            requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.QUANTITY.toString(), String.valueOf(oldQuantity), String.valueOf(requirement.getQuantity()), FdpRequirementEventType.CONTROL_POLICY_QUANTITY_OVERRIDE.toString(), "CaseSize policy applied", "system"));
            requirementChangeRequest.setRequirement(requirement);
            requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
            requirementChangeRequestList.add(requirementChangeRequest);
        }
    }

    private double parsePolicy(String value, String key) {
        TypeReference<Map<String, Double>> typeReference = new TypeReference<Map<String, Double>>() {
        };
        Map<String, Double> policyMap = super.parsePolicy(value, typeReference);
        if (policyMap != null && policyMap.containsKey(key)) {
            return policyMap.get(key);
        }
        return 0D;
    }

    public static boolean isValidCaseSize(double value) {
        if (value < Constants.MIN_CASE_SIZE) {
            return false;
        } else {
            return true;
        }
    }
}
