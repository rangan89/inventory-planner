package fk.retail.ip.requirement.internal.context;

import com.google.common.collect.Lists;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PolicyApplicator {

    protected final ObjectMapper objectMapper;

    public PolicyApplicator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected double convertDaysToQuantity(Double days, List<Double> forecast) {
        int i = 0;
        double quantity = 0;
        for (double remainingDays = days; remainingDays > 0; remainingDays -= Constants.DAYS_IN_WEEK, i++) {
            if (remainingDays >= Constants.DAYS_IN_WEEK) {
                quantity += forecast.get(i);
            } else {
                quantity += forecast.get(i) * remainingDays / Constants.DAYS_IN_WEEK;
            }
        }
        return quantity;
    }

    abstract void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext, List<RequirementChangeRequest> requirementChangeRequestList);

    public <T> T parsePolicy(String value, TypeReference<T> typeReference) {
        if (value != null) {
            try {
                T policy = objectMapper.readValue(value, typeReference);
                return policy;
            } catch (IOException e) {
                log.warn(Constants.UNABLE_TO_PARSE, value);
            }
        }
        return null;
    }

    public void addToSnapshot(Requirement requirement, PolicyType type, double value) {
        String appliedPoliciesString = requirement.getRequirementSnapshot().getPolicy();
        List<String> appliedPolicies = Lists.newArrayList();
        if (appliedPoliciesString != null) {
            appliedPolicies.addAll(Arrays.asList(requirement.getRequirementSnapshot().getPolicy().split(",")));
        }
        appliedPolicies.add(String.format(Constants.POLICY_DISPLAY_FORMAT, type, value));
        requirement.getRequirementSnapshot().setPolicy(String.join(",", appliedPolicies));
    }


    public void markAsError(Requirement requirement, String errorMessage) {
        requirement.setState(RequirementApprovalState.ERROR.toString());
        requirement.setEnabled(true);
        requirement.setCurrent(true);
        requirement.setOverrideComment(errorMessage);
    }
}
