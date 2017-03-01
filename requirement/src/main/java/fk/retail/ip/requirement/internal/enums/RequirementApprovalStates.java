package fk.retail.ip.requirement.internal.enums;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementApprovalStates {

    //todo: 23/02/17 change enum names to new states

    PRE_PROPOSED("proposed"),
    PROPOSED("verified"),
    CDO_REVIEW("approved"),
    BIZFIN_REVIEW("bd_approved"),
    IPC_REVIEW("bizfin_approved"),
    IPC_FINALISED("ipc_finalized");

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
