package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Policy;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;

public interface PolicyRepository extends JpaGenericRepository<Policy, Long> {

    List<Policy> fetchByFsns(List<String> fsns);

    List<Policy> fetchByGroup(List<Long> ids);
}
