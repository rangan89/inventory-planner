package fk.retail.ip.requirement.internal.factory;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.exception.InvalidRequirementStateException;
import fk.retail.ip.requirement.internal.states.*;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class RequirementStateFactory {

    private final PreProposedRequirementState preProposedRequirementState;
    private final ProposedRequirementState proposedRequirementState;
    private final BizFinRequirementState bizFinRequirementState;
    private final CDOReviewRequirementState cdoReviewRequirementState;
    private final IPCReviewRequirementState ipcReviewRequirementState;
    private final IPCFinalisedRequirementState ipcFinalisedRequirementState;

    @Inject
    public RequirementStateFactory(PreProposedRequirementState preProposedRequirementState, ProposedRequirementState proposedRequirementState, BizFinRequirementState bizFinRequirementState,
                                   CDOReviewRequirementState cdoReviewRequirementState, IPCReviewRequirementState ipcReviewRequirementState,
                                   IPCFinalisedRequirementState ipcFinalisedRequirementState) {
        this.preProposedRequirementState = preProposedRequirementState;
        this.proposedRequirementState = proposedRequirementState;
        this.cdoReviewRequirementState = cdoReviewRequirementState;
        this.bizFinRequirementState = bizFinRequirementState;
        this.ipcReviewRequirementState = ipcReviewRequirementState;
        this.ipcFinalisedRequirementState = ipcFinalisedRequirementState;
    }

    public RequirementState getRequirementState(String requirementState) throws InvalidRequirementStateException {

        RequirementApprovalState requirementApprovalState = RequirementApprovalState.fromString(requirementState);
        if (requirementApprovalState == null) {
            throw new InvalidRequirementStateException(requirementState + " is not a valid requirement state");

        }
        switch (requirementApprovalState) {
            case PRE_PROPOSED:
                return preProposedRequirementState;
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
