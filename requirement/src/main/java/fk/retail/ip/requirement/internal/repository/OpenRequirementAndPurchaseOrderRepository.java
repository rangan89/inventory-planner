package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;

public interface OpenRequirementAndPurchaseOrderRepository extends JpaGenericRepository<OpenRequirementAndPurchaseOrder, Long> {

    List<OpenRequirementAndPurchaseOrder> fetchByFsns(List<String> fsns);
}
