package fk.retail.ip.requirement.internal.enums;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementApprovalStates {

    PROPOSED("proposed"),
    CDO_REVIEW("CDOReview"),
    BIZFIN_REVIEW("BizFinReview"),
    IPC_REVIEW("IPCReview"),
    IPC_FINALISED("IPCFinalised");

    private String state;

    RequirementApprovalStates(String state) {
        this.state = state;
    }

    @Override
    public String toString() { return this.state;}

    public static RequirementApprovalStates fromString(String state) {
        for (RequirementApprovalStates approvalState : RequirementApprovalStates.values()) {
            if (approvalState.state.equalsIgnoreCase(state)) {
                return approvalState;
            }
        }
        return null;
    }

}
