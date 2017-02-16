package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

import java.util.List;
import java.util.Set;
/**
 * Created by yogeshwari.k on 16/02/17.
 */
public interface LastAppSupplierRepository extends JpaGenericRepository<LastAppSupplier, Long> {
    List<LastAppSupplier> fetchLastAppSupplierForFsns(Set<String> fsns);
}
