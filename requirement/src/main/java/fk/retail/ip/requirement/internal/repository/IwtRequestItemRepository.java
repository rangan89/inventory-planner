package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;

public interface IwtRequestItemRepository extends JpaGenericRepository<IwtRequestItem, Long> {

    List<IwtRequestItem> fetchByFsns(List<String> fsns, List<String> statuses);
}
