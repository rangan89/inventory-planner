package fk.retail.ip.requirement.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import fk.retail.ip.requirement.internal.repository.ForecastRepository;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.JPAForecastRepository;
import fk.retail.ip.requirement.internal.repository.JPAFsnBandRepository;
import fk.retail.ip.requirement.internal.repository.JPAGroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.JPALastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.JPAOpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.JPAPolicyRepository;
import fk.retail.ip.requirement.internal.repository.JPAProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.JPARequirementRepository;
import fk.retail.ip.requirement.internal.repository.JPAWarehouseInventoryRepository;
import fk.retail.ip.requirement.internal.repository.JPAWarehouseRepository;
import fk.retail.ip.requirement.internal.repository.JPAWeeklySaleRepository;
import fk.retail.ip.requirement.internal.repository.JpaIwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.ProjectionRepository;
import fk.retail.ip.requirement.internal.repository.ProjectionRepositoryImpl;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseInventoryRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseRepository;
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
        bind(ProductInfoRepository.class).to(JPAProductInfoRepository.class);
        bind(WarehouseRepository.class).to(JPAWarehouseRepository.class);
        bind(WarehouseInventoryRepository.class).to(JPAWarehouseInventoryRepository.class);
        bind(ForecastRepository.class).to(JPAForecastRepository.class);
        //TODO:remove
        bind(ProjectionRepository.class).to(ProjectionRepositoryImpl.class);

        bind(String.class).annotatedWith(Names.named("actionConfiguration")).toInstance("/requirement-state-actions.json");
    }
}
