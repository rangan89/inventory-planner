package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PolicyContext {

    private final ObjectMapper objectMapper;
    private final Map<String, String> warehouseCodeMap;
    private final List<PolicyApplicator> orderedPolicyApplicators;
    private Table<String, PolicyType, String> fsnPolicyTypeDataTable = HashBasedTable.create();

    public PolicyContext(ObjectMapper objectMapper, Map<String, String> warehouseCodeMap) {
        this.objectMapper = objectMapper;
        this.warehouseCodeMap = warehouseCodeMap;
        //DO NOT CHANGE THE ORDERING UNLESS YOU KNOW WHAT YOU ARE DOING
        orderedPolicyApplicators = Lists.newArrayList(new RopRocApplicator(objectMapper), new MaxCoverageApplicator(objectMapper), new CaseSizeApplicator(objectMapper));
    }

    public Set<String> getFsns() {
        return fsnPolicyTypeDataTable.rowKeySet();
    }

    public String addPolicy(String fsn, String policyType, String value) {
        //to replace wh name by wh code in policy values (case insensitive)
        for (String name : warehouseCodeMap.keySet()) {
            value = value.replaceAll("(?i)".concat(name), warehouseCodeMap.get(name));
        }
        return fsnPolicyTypeDataTable.put(fsn, PolicyType.fromString(policyType), value);
    }

    public void applyPolicies(String fsn, List<Requirement> requirements, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
        orderedPolicyApplicators.forEach(policyApplicator -> policyApplicator.applyPolicies(fsn, requirements, fsnPolicyTypeDataTable.row(fsn), forecastContext, onHandQuantityContext));
    }

    public String getPolicyAsString(String fsn) {
        String policyString = null;
        try {
            policyString = objectMapper.writeValueAsString(fsnPolicyTypeDataTable.row(fsn));
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
        }
        return policyString;
    }
}
