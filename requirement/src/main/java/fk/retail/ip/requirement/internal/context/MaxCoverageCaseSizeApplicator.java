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
        if (policyTypeMap.containsKey(PolicyType.MAX_COVERAGE) && policyTypeMap.get(PolicyType.MAX_COVERAGE) != null) {
            double maxCoverageQuantity = convertDaysToQuantity(parsePolicy(policyTypeMap.get(PolicyType.MAX_COVERAGE), PolicyType.MAX_COVERAGE), forecastContext.geAllIndiaForecast(fsn));
            double totalOnHandQuantity = onHandQuantityContext.getTotalQuantity(fsn);
            double totalProjectedQuantity = requirements.stream().mapToDouble(Requirement::getQuantity).sum();
            if (maxCoverageQuantity > totalProjectedQuantity) {
                double reductionRatio = (maxCoverageQuantity - totalOnHandQuantity) / totalProjectedQuantity;
                requirements.forEach(requirement -> {
                    double reducedQuantity = requirement.getQuantity() * reductionRatio;
                    requirement.setQuantity(reducedQuantity);
                });
            }
        }
        if (policyTypeMap.containsKey(PolicyType.CASE_SIZE) && policyTypeMap.get(PolicyType.CASE_SIZE) != null) {
            int caseSize = (int) parsePolicy(policyTypeMap.get(PolicyType.CASE_SIZE), PolicyType.CASE_SIZE);
            if (caseSize != 0) {
                if (policyTypeMap.containsKey(PolicyType.MAX_COVERAGE) && policyTypeMap.get(PolicyType.MAX_COVERAGE) != null) {
                    //max coverage is present, round everything down
                    requirements.forEach(requirement -> {
                        double roundedQuantity = Math.floor(requirement.getQuantity()/caseSize) * caseSize;
                        requirement.setQuantity(roundedQuantity);
                    });
                } else {
                    //round to nearest multiple of case size
                    requirements.forEach(requirement -> {
                        double roundedQuantity = Math.round(requirement.getQuantity()/caseSize) * caseSize;
                        requirement.setQuantity(roundedQuantity);
                    });
                }
            }
        }
    }

    private double parsePolicy(String value, PolicyType type) {
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
            log.error(Constants.UNABLE_TO_PARSE, value);
        }
        return 0.0;
    }
}
