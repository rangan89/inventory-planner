package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Projection;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

@Deprecated
//TODO: remove
public interface ProjectionRepository extends JpaGenericRepository<Projection, Long> {
    List<Projection> find(Set<String> fsns, boolean enabled);
}
