package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.exception.InvalidRequirementStateException;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStates;
import fk.retail.ip.requirement.internal.states.BizFinRequirementState;
import fk.retail.ip.requirement.internal.states.CDOReviewRequirementState;
import fk.retail.ip.requirement.internal.states.IPCFinalisedRequirementState;
import fk.retail.ip.requirement.internal.states.IPCReviewRequirementState;
import fk.retail.ip.requirement.internal.states.ProposedRequirementState;
import fk.retail.ip.requirement.internal.states.RequirementState;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class RequirementStateFactory {

    private final ProposedRequirementState proposedRequirementState;
    private final BizFinRequirementState bizFinRequirementState;
    private final CDOReviewRequirementState cdoReviewRequirementState;
    private final IPCReviewRequirementState ipcReviewRequirementState;
    private final IPCFinalisedRequirementState ipcFinalisedRequirementState;

    @Inject
    public RequirementStateFactory(ProposedRequirementState proposedRequirementState, BizFinRequirementState bizFinRequirementState,
                                   CDOReviewRequirementState cdoReviewRequirementState, IPCReviewRequirementState ipcReviewRequirementState,
                                   IPCFinalisedRequirementState ipcFinalisedRequirementState) {
        this.proposedRequirementState = proposedRequirementState;
        this.cdoReviewRequirementState = cdoReviewRequirementState;
        this.bizFinRequirementState = bizFinRequirementState;
        this.ipcReviewRequirementState = ipcReviewRequirementState;
        this.ipcFinalisedRequirementState = ipcFinalisedRequirementState;
    }

    public RequirementState getRequirementState(String requirementState) throws InvalidRequirementStateException {

        RequirementApprovalStates requirementApprovalState = RequirementApprovalStates.fromString(requirementState);
        if (requirementApprovalState == null) {
            throw new InvalidRequirementStateException(requirementState + " is not a valid requirement state");

        }
        switch (requirementApprovalState) {
            case PROPOSED:
                return proposedRequirementState;
            case CDO_REVIEW:
                return cdoReviewRequirementState;
            case BIZFIN_REVIEW:
                return bizFinRequirementState;
            case IPC_REVIEW:
                return ipcReviewRequirementState;
            case IPC_FINALISED:
                return ipcFinalisedRequirementState;
        }

        return null;

    }
}
