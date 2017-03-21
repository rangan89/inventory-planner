package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.ProcPurchaseOrder;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

public interface ProcPurchaseOrderRepository extends JpaGenericRepository<ProcPurchaseOrder, Long> {

    String find(String fsn);
}
