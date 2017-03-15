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
public class CaseSizeApplicator extends PolicyApplicator {

    public CaseSizeApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
        Double maxCoverageDays = parsePolicy(policyTypeMap.get(PolicyType.MAX_COVERAGE), Constants.MAX_COVERAGE_KEY);
        Double caseSize = parsePolicy(policyTypeMap.get(PolicyType.CASE_SIZE), Constants.CASE_SIZE_KEY);
        if (isValidCaseSize(caseSize)) {
            if (MaxCoverageApplicator.isValidMaxCoverage(maxCoverageDays)) {
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
