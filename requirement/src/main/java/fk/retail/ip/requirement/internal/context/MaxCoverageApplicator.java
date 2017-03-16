package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaxCoverageApplicator extends PolicyApplicator {

    public MaxCoverageApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
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
