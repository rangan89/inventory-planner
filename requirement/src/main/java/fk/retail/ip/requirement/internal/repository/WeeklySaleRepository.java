package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 15/02/17.
 */
public interface WeeklySaleRepository extends JpaGenericRepository<WeeklySale, Long> {

    List<WeeklySale> fetchWeeklySalesForFsns(Set<String> fsns);
}
