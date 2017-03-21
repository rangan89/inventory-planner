package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.WarehouseSupplierSla;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.Optional;

public interface WarehouseSupplierSlaRepository extends JpaGenericRepository<WarehouseSupplierSla, Long> {

    Optional<Integer> getSla(String vertical, String warehouse, String supplier);

    Optional<Integer> getSla(String vertical, String warehouse);

    Optional<Integer> getSla(String vertical);
}
