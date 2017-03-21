package fk.retail.ip.requirement.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.JPAGroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.ssl.client.HystrixSslClient;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.joda.time.DateTime;

@Path("/test")
@Transactional
public class TestResource {

    private final IwtRequestItemRepository iwtRequestItemRepository;
    private final OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository;
    private final PolicyRepository policyRepository;
    private final GroupFsnRepository groupFsnRepository;
    private final RequirementRepository requirementRepository;
    private final HystrixSslClient hystrixSslClient;

    @Inject
    public TestResource(HystrixSslClient hystrixSslClient, IwtRequestItemRepository iwtRequestItemRepository, OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository, PolicyRepository policyRepository, JPAGroupFsnRepository jpaGroupFsnRepository, RequirementRepository requirementRepository) {
        this.iwtRequestItemRepository = iwtRequestItemRepository;
        this.openRequirementAndPurchaseOrderRepository = openRequirementAndPurchaseOrderRepository;
        this.policyRepository = policyRepository;
        this.groupFsnRepository = jpaGroupFsnRepository;
        this.hystrixSslClient = hystrixSslClient;
        this.requirementRepository = requirementRepository;
    }

    @GET
    @Path("/iwt_items")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IwtRequestItem> getIwtRequestItems(@QueryParam("fsn") String fsn) {
        return iwtRequestItemRepository.fetchByFsns(Sets.newHashSet(fsn), Constants.INTRANSIT_REQUEST_STATUSES);
    }

    @GET
    @Path("/open_po")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OpenRequirementAndPurchaseOrder> getOpenPo(@QueryParam("fsn") String fsn) {
        return openRequirementAndPurchaseOrderRepository.fetchByFsns(Sets.newHashSet(fsn));
    }

    @GET
    @Path("/policy")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Policy> getPolicy(@QueryParam("fsn") String fsn, @QueryParam("group_id") long id) {
        if (fsn != null) {
            return policyRepository.fetchByFsns(Sets.newHashSet(fsn));
        } else {
            return policyRepository.fetchByGroup(Sets.newHashSet(id));
        }
    }

    @GET
    @Path("/group_fsn")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GroupFsn> getGroupFsn(@QueryParam("fsn") String fsn) {
        return groupFsnRepository.findByFsns(Sets.newHashSet(fsn));
    }

    @GET
    @Path("/requirement")
    @Produces(MediaType.APPLICATION_JSON)
    public void getRequirement(@QueryParam("fsn") String fsn) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setWarehouse("dummy");
        requirement.setState(RequirementApprovalState.PROPOSED.toString());
        requirement.setEnabled(true);
        requirement.setCurrent(true);
        requirement.setQuantity(0.123);
//        requirement.setProjection(32749L);
        //TODO: do we need procType here?
        requirement.setProcType("DAILY_PLANNING");
        RequirementSnapshot requirementSnapshot = new RequirementSnapshot();
//        requirementSnapshot.setGroup(group);
//        requirementSnapshot.setPolicy(policyContext.getPolicyAsString(fsn, warehouse));
//        requirementSnapshot.setForecast(forecastContext.getForecastAsString(fsn, warehouse));
//        requirementSnapshot.setOpenReqQty((int) onHandQuantityContext.getOpenRequirementQuantity(fsn, warehouse));
//        requirementSnapshot.setPendingPoQty((int) onHandQuantityContext.getPendingPurchaseOrderQuantity(fsn, warehouse));
//        requirementSnapshot.setIwitIntransitQty((int) onHandQuantityContext.getIwtQuantity(fsn, warehouse));
//        requirementSnapshot.setInventoryQty((int) onHandQuantityContext.getInventoryQuantity(fsn, warehouse));
        requirement.setRequirementSnapshot(requirementSnapshot);
        requirementRepository.persist(requirement);
//        return requirement;
//        return groupFsnRepository.findByFsns(Sets.newHashSet(fsn));
    }

    @GET
    @Path("/ssl")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SupplierSelectionResponse> getSupplier() {
        RequirementSnapshot snapshot = new RequirementSnapshot();
        snapshot.setForecast("[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]");
        snapshot.setInventoryQty(10);
        snapshot.setQoh(10);
        snapshot.setPendingPoQty(10);
        snapshot.setOpenReqQty(10);
        snapshot.setIwitIntransitQty(10);

        List<Requirement> requirements = Lists.newArrayList();
        Requirement requirement = new Requirement();
        requirement.setFsn("TVSDWJFGWNPFJS7M");
        requirement.setState("proposed");
        requirement.setEnabled(true);
        //requirement.setWarehouse("del");
        requirement.setRequirementSnapshot(snapshot);
        requirement.setQuantity(358);
        requirement.setSupplier("supplier1");
        requirement.setMrp(100);
        requirement.setApp(120);
        requirement.setCurrency("INR");
        requirement.setSla(10);
        requirement.setOverrideComment("comment1");
        requirement.setProcType("DAILY_PLANNING");
        requirements.add(requirement);
        List<SupplierSelectionRequest> requests = getSupplierSelectionRequest(requirements);
        List<SupplierSelectionResponse> responses = hystrixSslClient.getSupplierSelectionResponse(requests);
        return responses;

    }

    public List<SupplierSelectionRequest> getSupplierSelectionRequest(List<Requirement> requirements) {
        List<SupplierSelectionRequest> requests = Lists.newArrayList();
        requirements.forEach(req -> {
            SupplierSelectionRequest request = new SupplierSelectionRequest();
            request.setFsn(req.getFsn());
            request.setSku("SKU0000000000000");
            request.setOrderType(req.getProcType());
            request.setQuantity((int) req.getQuantity());
            request.setEntityType("Requirement");
            request.setWarehouseId(req.getWarehouse());
            request.setTenantId("WSR");
            int sla = req.getSla();
            DateTime date = DateTime.now().plusDays(sla);
            request.setRequiredByDate(date.toString());
            requests.add(request);
        });
        return requests;
    }
}
