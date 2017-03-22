package fk.retail.ip.requirement.internal.enums;

/**
 * Created by agarwal.vaibhav on 07/03/17.
 */
public enum OverrideStatus {
    FAILURE("failure"),
    SUCCESS("success"),
    UPDATE("update");

    private String status;

    OverrideStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() { return this.status;}

    public static OverrideStatus fromString(String status) {
        for (OverrideStatus state : OverrideStatus.values()) {
            if(state.status.equalsIgnoreCase(status)) {
                return state;
            }
        }
        return null;
    }
}
