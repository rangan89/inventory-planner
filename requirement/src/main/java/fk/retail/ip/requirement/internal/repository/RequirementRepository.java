package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;

/**
 * Created by nidhigupta.m on 15/02/17.
 */
public interface RequirementRepository extends JpaGenericRepository<Requirement, Long> {

    List<Requirement> findRequirementByIds(List<Long> requirementIds);

    List<Requirement> findAllEnabledRequirements(String state);
}
