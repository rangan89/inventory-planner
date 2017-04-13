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
public class MaxCoverageApplicator extends PolicyApplicator {

    public MaxCoverageApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext, List<RequirementChangeRequest> requirementChangeRequestList) {
        Double maxCoverageDays = parsePolicy(policyTypeMap.get(PolicyType.MAX_COVERAGE), Constants.MAX_COVERAGE_KEY);
        if (isValidMaxCoverage(maxCoverageDays)) {
            double maxCoverageQuantity = convertDaysToQuantity(maxCoverageDays, forecastContext.getForecast(fsn));
            double totalOnHandQuantity = onHandQuantityContext.getTotalQuantity(fsn);
            double totalProjectedQuantity = requirements.stream().filter(requirement -> !Constants.ERROR_STATE.equals(requirement.getState()) && fsn.equals(requirement.getFsn())).mapToDouble(Requirement::getQuantity).sum();
            if (maxCoverageQuantity < totalProjectedQuantity + totalOnHandQuantity) {
                double reductionRatio = (maxCoverageQuantity - totalOnHandQuantity) / totalProjectedQuantity;
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.MAX_COVERAGE, maxCoverageDays);
                    double reducedQuantity = requirement.getQuantity() * reductionRatio;
                    reducedQuantity = reducedQuantity > 0 ? reducedQuantity : 0;
                    //Add CONTROL_POLICY_QUANTITY_OVERRIDE events to fdp request
                    log.info("Adding CONTROL_POLICY_QUANTITY_OVERRIDE(MaxCoverage) events to fdp request");
                    RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                    List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
                    requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.QUANTITY.toString(), String.valueOf(requirement.getQuantity()), String.valueOf(reducedQuantity),FdpRequirementEventType.CONTROL_POLICY_QUANTITY_OVERRIDE.toString(), "MaxCoverage policy applied", "system"));
                    requirementChangeRequest.setRequirement(requirement);
                    requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                    requirementChangeRequestList.add(requirementChangeRequest);
                    requirement.setQuantity(reducedQuantity);
                });
            }
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

    public static boolean isValidMaxCoverage(double value) {
        if (value <= 0 || value > Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK) {
            return false;
        } else {
            return true;
        }
    }
}
