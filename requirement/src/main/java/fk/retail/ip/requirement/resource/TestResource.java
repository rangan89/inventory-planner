package fk.retail.ip.requirement.resource;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
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

    @Inject
    public TestResource(IwtRequestItemRepository iwtRequestItemRepository) {
        this.iwtRequestItemRepository = iwtRequestItemRepository;
    }

    @GET
    @Path("/iwt_items")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IwtRequestItem> getIwtRequestItems(@QueryParam("fsn") String fsn) {
        return iwtRequestItemRepository.fetchIwtIntransitItems(fsn);
//    return Lists.newArrayList(iwtRequestItemRepository.findOne(944441L).get());
    }

}
