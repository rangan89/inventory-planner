package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

public interface OpenRequirementAndPurchaseOrderRepository extends JpaGenericRepository<OpenRequirementAndPurchaseOrder, Long> {

    List<OpenRequirementAndPurchaseOrder> fetchByFsns(Set<String> fsns);
}
