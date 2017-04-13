package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.RequirementApprovalTransition;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;

/**
 * Created by nidhigupta.m on 24/03/17.
 */
public interface RequirementApprovalTransitionRepository extends JpaGenericRepository<RequirementApprovalTransition, Long> {


    List<RequirementApprovalTransition> getApprovalTransition(String fromState, boolean forward);
}
