package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 24/03/17.
 */
@Entity
@Data
@NoArgsConstructor
public class RequirementApprovalTransition extends ReadOnlyEntity {
    long groupId;
    String fromState;
    String toState;
    boolean forward;
}
