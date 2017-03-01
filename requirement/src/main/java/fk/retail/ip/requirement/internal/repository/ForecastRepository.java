package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.Forecast;
import fk.sp.common.extensions.jpa.JpaGenericRepository;
import java.util.List;
import java.util.Set;

public interface ForecastRepository extends JpaGenericRepository<Forecast, Long>{
    List<Forecast> fetchByFsns(Set<String> fsns);
}
