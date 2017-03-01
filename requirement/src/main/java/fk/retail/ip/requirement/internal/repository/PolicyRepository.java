package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

public interface PolicyRepository extends JpaGenericRepository<Policy, Long> {

    List<Policy> fetchByFsns(Set<String> fsns);

    List<Policy> fetchByGroup(Set<Long> ids);
}
