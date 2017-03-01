package fk.retail.ip.requirement.internal.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import java.util.List;
import java.util.Set;

public class PolicyContext {

    private final ObjectMapper objectMapper;
    private final List<PolicyApplicator> orderedPolicyApplicators;
    private Table<String, PolicyType, String> fsnPolicyTypeDataTable = HashBasedTable.create();

    public PolicyContext(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        //DO NOT CHANGE THE ORDERING UNLESS YOU KNOW WHAT YOU ARE DOING
        orderedPolicyApplicators = Lists.newArrayList(new RopRocApplicator(objectMapper), new MaxCoverageCaseSizeApplicator(objectMapper));
    }

    public Set<String> getFsns() {
        return fsnPolicyTypeDataTable.rowKeySet();
    }

    public String addPolicy(String fsn, String policyType, String value) {
        return fsnPolicyTypeDataTable.put(fsn, PolicyType.fromString(policyType), value);
    }

    public void applyPolicies(String fsn, List<Requirement> requirements, ForecastContext forecastContext, OnHandQuantityContext onHandQuantityContext) {
        orderedPolicyApplicators.forEach(policyApplicator -> policyApplicator.applyPolicies(fsn, requirements, fsnPolicyTypeDataTable.row(fsn), forecastContext, onHandQuantityContext));
    }

    public String getPolicyAsString(String fsn, String warehouse) {
        return null;
    }
}
