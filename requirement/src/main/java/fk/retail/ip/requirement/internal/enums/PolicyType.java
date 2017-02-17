package fk.retail.ip.requirement.internal.enums;

public enum PolicyType {

    PLANNING_CYCLE("PlanningCycle"), ROP("Rop"), ROC("Roc"), MAX_COVERAGE("MaxCoverage"), MIN_INVENTORY("MinInventory"), CASE_SIZE("CaseSize");
    String value;

    private PolicyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
