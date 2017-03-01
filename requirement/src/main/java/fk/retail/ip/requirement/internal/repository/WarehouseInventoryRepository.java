package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.WarehouseInventory;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

public interface WarehouseInventoryRepository extends JpaGenericRepository<WarehouseInventory, Long> {
    List<WarehouseInventory> fetchByFsns(Set<String> fsns);
}
