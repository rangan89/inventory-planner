package fk.retail.ip.requirement.internal.repository;

import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 15/02/17.
 */
public interface FsnBandRepository extends JpaGenericRepository<FsnBand, Long> {

    List<FsnBand> fetchBandDataForFSNs(Set<String> fsns);

}
