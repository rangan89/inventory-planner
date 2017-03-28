package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

public interface GroupFsnRepository extends JpaGenericRepository<GroupFsn, Long> {
    List<GroupFsn> findByFsns(Set<String> fsns);

    List<String> getFsns(String group);

    List<String> getAllFsns();
}
