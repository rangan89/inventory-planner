package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RopRocApplicator extends PolicyApplicator {

    public RopRocApplicator(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void applyPolicies(String fsn, List<Requirement> requirements, Map<PolicyType, String> policyTypeMap, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
        Map<String, Double> warehouseToRopMap = parseRopRoc(policyTypeMap.get(PolicyType.ROP));
        Map<String, Double> warehouseToRocMap = parseRopRoc(policyTypeMap.get(PolicyType.ROC));
        requirements.stream().filter(requirement -> !Constants.ERROR_STATE.equals(requirement.getState())).forEach(requirement -> {
            String warehouse = requirement.getWarehouse();
            List<Double> forecast = forecastContext.getForecast(fsn, warehouse);
            Double ropDays = warehouseToRopMap.get(warehouse);
            if (ropDays == null) {
                //rop policy not found
                requirement.setState(Constants.ERROR_STATE);
                requirement.setEnabled(false);
                requirement.setOverrideComment(String.format(Constants.POLICY_NOT_FOUND, PolicyType.ROP));
                return;
            }
            double ropQuantity = convertDaysToQuantity(ropDays, forecast);
            double onHandQuantity = onHandQuantityContext.getTotalQuantity(fsn, warehouse);
            if (onHandQuantity <= ropQuantity) {
                //reorder point has been reached
                Double rocDays = warehouseToRocMap.get(warehouse);
                if (rocDays == null) {
                    //roc policy not found
                    requirement.setState(Constants.ERROR_STATE);
                    requirement.setEnabled(false);
                    requirement.setOverrideComment(String.format(Constants.POLICY_NOT_FOUND, PolicyType.ROC));
                    return;
                }
                double demand = convertDaysToQuantity(rocDays, forecast);
                requirement.setQuantity(demand - onHandQuantity);
            }
        });
    }

    private Map<String, Double> parseRopRoc(String value) {
        Map<String, Double> policyMap = Maps.newHashMap();
        TypeReference<Map<String, Map<String, Integer>>> typeReference = new TypeReference<Map<String, Map<String, Integer>>>() {};
        try {
            Map<String, Map<String, Double>> rawMap = objectMapper.readValue(value, typeReference);
            rawMap.entrySet().stream().forEach(entry -> policyMap.put(entry.getKey(), entry.getValue().get("days")));
        } catch (IOException e) {
            log.error(Constants.UNABLE_TO_PARSE, value);
        }
        return policyMap;
    }
}
