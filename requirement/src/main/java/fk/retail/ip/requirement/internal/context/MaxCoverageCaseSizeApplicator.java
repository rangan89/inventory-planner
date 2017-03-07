package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaxCoverageCaseSizeApplicator extends PolicyApplicator {

    public MaxCoverageCaseSizeApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
        Double maxCoverageDays = parsePolicy(policyTypeMap.get(PolicyType.MAX_COVERAGE), PolicyType.MAX_COVERAGE);
        if (isValidMaxCoverage(maxCoverageDays)) {
            double maxCoverageQuantity = convertDaysToQuantity(maxCoverageDays, forecastContext.geAllIndiaForecast(fsn));
            double totalOnHandQuantity = onHandQuantityContext.getTotalQuantity(fsn);
            double totalProjectedQuantity = requirements.stream().mapToDouble(Requirement::getQuantity).sum();
            if (maxCoverageQuantity < totalProjectedQuantity) {
                double reductionRatio = (maxCoverageQuantity - totalOnHandQuantity) / totalProjectedQuantity;
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.MAX_COVERAGE, maxCoverageDays);
                    double reducedQuantity = requirement.getQuantity() * reductionRatio;
                    requirement.setQuantity(reducedQuantity);
                });
            }
        }
        Double caseSize = parsePolicy(policyTypeMap.get(PolicyType.CASE_SIZE), PolicyType.CASE_SIZE);
        if (isValidCaseSize(caseSize)) {
            if (isValidMaxCoverage(maxCoverageDays)) {
                //max coverage is present, round everything down
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.CASE_SIZE, caseSize);
                    double roundedQuantity = Math.floor(requirement.getQuantity() / caseSize) * caseSize;
                    requirement.setQuantity(roundedQuantity);
                });
            } else {
                //round to nearest multiple of case size
                requirements.forEach(requirement -> {
                    addToSnapshot(requirement, PolicyType.CASE_SIZE, caseSize);
                    double roundedQuantity = Math.round(requirement.getQuantity() / caseSize) * caseSize;
                    requirement.setQuantity(roundedQuantity);
                });
            }

        }
    }

    private Double parsePolicy(String value, PolicyType type) {
        if (value == null) {
            return null;
        }
        TypeReference<Map<String, Double>> typeReference = new TypeReference<Map<String, Double>>() {
        };
        String key = "";
        if (type == PolicyType.MAX_COVERAGE) {
            key = "max_coverage";
        }
        if (type == PolicyType.CASE_SIZE) {
            key = "case_size";
        }
        try {
            Map<String, Double> policyMap = objectMapper.readValue(value, typeReference);
            return policyMap.get(key);
        } catch (IOException e) {
            log.warn(Constants.UNABLE_TO_PARSE, value);
        }
        return 0.0;
    }

    public boolean isValidMaxCoverage(Double value) {
        if (value == null) {
            return false;
        } else if (value <= 0 || value > Constants.WEEKS_OF_FORECAST * Constants.DAYS_IN_WEEK) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidCaseSize(Double value) {
        if (value == null) {
            return false;
        } else if (value <= 0) {
            return false;
        } else {
            return true;
        }
    }
}
