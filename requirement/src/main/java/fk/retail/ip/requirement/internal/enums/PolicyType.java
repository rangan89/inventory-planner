package fk.retail.ip.requirement.internal.enums;

import fk.retail.ip.requirement.internal.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PolicyType {

    PLANNING_CYCLE("PlanningCycle"), ROP("Rop"), ROC("Roc"), MAX_COVERAGE("MaxCoverage"), CASE_SIZE("CaseSize"), UNKNOWN("Unknown");
    String value;

    private PolicyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static PolicyType fromString(String value) {
        for (PolicyType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        log.warn(Constants.INVALID_POLICY_TYPE, value);
        return UNKNOWN;
    }
}
