package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Map;

public abstract class PolicyApplicator {

    protected final ObjectMapper objectMapper;

    public PolicyApplicator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected double convertDaysToQuantity(Double days, List<Double> forecast) {
        int i = 0;
        double quantity = 0;
        for (double remainingDays = days; remainingDays > 0; remainingDays -= Constants.DAYS_IN_WEEK) {
            if (remainingDays >= Constants.DAYS_IN_WEEK) {
                quantity += forecast.get(i);
            } else {
                quantity *= forecast.get(i)*remainingDays/Constants.DAYS_IN_WEEK;
            }
        }
        return quantity;
    }

    abstract void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext);
}
