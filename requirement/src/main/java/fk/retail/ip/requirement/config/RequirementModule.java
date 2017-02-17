package fk.retail.ip.requirement.config;

import com.google.inject.AbstractModule;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.JPAFsnBandRepository;
import fk.retail.ip.requirement.internal.repository.JPAGroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.JPALastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.JPAOpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.JPAPolicyRepository;
import fk.retail.ip.requirement.internal.repository.JPARequirementRepository;
import fk.retail.ip.requirement.internal.repository.JPAWeeklySaleRepository;
import fk.retail.ip.requirement.internal.repository.JpaIwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.resource.RequirementResource;
import fk.retail.ip.requirement.resource.TestResource;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(RequirementResource.class);
        bind(TestResource.class);

        bind(FsnBandRepository.class).to(JPAFsnBandRepository.class);
        bind(WeeklySaleRepository.class).to(JPAWeeklySaleRepository.class);
        bind(RequirementRepository.class).to(JPARequirementRepository.class);
        bind(LastAppSupplierRepository.class).to(JPALastAppSupplierRepository.class);
        bind(IwtRequestItemRepository.class).to(JpaIwtRequestItemRepository.class);
        bind(OpenRequirementAndPurchaseOrderRepository.class).to(JPAOpenRequirementAndPurchaseOrderRepository.class);
        bind(PolicyRepository.class).to(JPAPolicyRepository.class);
        bind(GroupFsnRepository.class).to(JPAGroupFsnRepository.class);
    }
}
