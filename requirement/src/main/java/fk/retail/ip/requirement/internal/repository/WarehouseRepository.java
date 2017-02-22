package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

/**
 * Created by yogeshwari.k on 21/02/17.
 */
public interface WarehouseRepository extends JpaGenericRepository<Warehouse, Long> {
    List<Warehouse> fetchWarehouseNameByCode(Set<String> whCodes);
}
