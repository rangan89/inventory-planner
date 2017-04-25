package fk.retail.ip.requirement.internal.repository;

import java.util.List;
import java.util.Set;

import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

public interface PolicyRepository extends JpaGenericRepository<Policy, Long> {

    List<Policy> fetch(Set<String> fsns);

    List<Policy> fetch(Set<String> fsns, PolicyType policyType);

    List<Policy> fetchByGroup(Set<Long> ids);

    List<Policy> fetchByGroup(Set<Long> ids, PolicyType policyType);
}
