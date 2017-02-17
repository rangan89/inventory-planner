package fk.retail.ip.requirement.resource;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.JPAGroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Transactional
public class TestResource {

    private final IwtRequestItemRepository iwtRequestItemRepository;
    private final OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository;
    private final PolicyRepository policyRepository;
    private final GroupFsnRepository groupFsnRepository;

    @Inject
    public TestResource(IwtRequestItemRepository iwtRequestItemRepository, OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository, PolicyRepository policyRepository, JPAGroupFsnRepository jpaGroupFsnRepository) {
        this.iwtRequestItemRepository = iwtRequestItemRepository;
        this.openRequirementAndPurchaseOrderRepository = openRequirementAndPurchaseOrderRepository;
        this.policyRepository = policyRepository;
        this.groupFsnRepository = jpaGroupFsnRepository;
    }

    @GET
    @Path("/iwt_items")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IwtRequestItem> getIwtRequestItems(@QueryParam("fsn") String fsn) {
        return iwtRequestItemRepository.fetchByFsns(Lists.newArrayList(fsn), Lists.newArrayList("in-process", "dispatched", "requested"));
    }

    @GET
    @Path("/open_po")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OpenRequirementAndPurchaseOrder> getOpenPo(@QueryParam("fsn") String fsn) {
        return openRequirementAndPurchaseOrderRepository.fetchByFsns(Lists.newArrayList(fsn));
    }

    @GET
    @Path("/policy")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Policy> getPolicy(@QueryParam("fsn") String fsn, @QueryParam("group_id") long id) {
        if (fsn != null) {
            return policyRepository.fetchByFsns(Lists.newArrayList(fsn));
        } else {
            return policyRepository.fetchByGroup(Lists.newArrayList(id));
        }
    }

    @GET
    @Path("/group_fsn")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GroupFsn> getGroupFsn(@QueryParam("fsn") String fsn) {
        return groupFsnRepository.findByFsns(Lists.newArrayList(fsn));
    }

}
