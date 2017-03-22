package fk.retail.ip.requirement.internal.enums;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementApprovalState {

    //todo: 23/02/17 change enum names to new states

    PRE_PROPOSED("proposed"),
    PROPOSED("verified"),
    CDO_REVIEW("approved"),
    BIZFIN_REVIEW("bd_approved"),
    IPC_REVIEW("bizfin_approved"),
    IPC_FINALISED("ipc_finalized"), ;



    private String state;

    RequirementApprovalState(String state) {
        this.state = state;
    }

    @Override
    public String toString() { return this.state;}

    public static RequirementApprovalState fromString(String state) {
        for (RequirementApprovalState approvalState : RequirementApprovalState.values()) {
            if (approvalState.state.equalsIgnoreCase(state)) {
                return approvalState;
            }
        }
        return null;
    }

}
