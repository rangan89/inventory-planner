package fk.retail.ip.requirement.internal.enums;

import lombok.Getter;

/**
 * Created by nidhigupta.m on 24/03/17.
 */

@Getter
public enum RequirementApprovalAction {
    verify(true),
    approve(true),
    bd_approve(true),
    bizfin_approve(true),
    ipc_finalize(true),
    cancel_verify(false),
    cancel_approve(false),
    cancel_bd_approve(false),
    cancel_bizfin_approve(false),
    cancel_ipc_finalize(false);

    boolean isForward;

    RequirementApprovalAction(boolean isForward) {
        this.isForward = isForward;
    }

}
