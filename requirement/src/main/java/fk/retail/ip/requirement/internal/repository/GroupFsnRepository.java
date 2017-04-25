package fk.retail.ip.requirement.internal.repository;

import java.util.List;
import java.util.Set;

import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

public interface GroupFsnRepository extends JpaGenericRepository<GroupFsn, Long> {
    List<GroupFsn> findByFsns(Set<String> fsns);

    List<String> getFsns(String group);

    List<String> getAllFsns();

    List<GroupFsn> findByGroupIds(Set<Long> groupIds);
}
