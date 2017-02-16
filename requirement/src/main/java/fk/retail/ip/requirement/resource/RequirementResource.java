package fk.retail.ip.requirement.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.service.RequirementService;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Path("/v1/requirement")
public class RequirementResource {

    private final RequirementService requirementService;

    @Inject
    public RequirementResource(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @POST
    @Path("/download")
    @Timed
    @UnitOfWork
    public Response download(DownloadRequirementRequest downloadRequirementRequest) {
        StreamingOutput stream = requirementService.downloadRequirement(downloadRequirementRequest);
        return Response.ok(stream)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = projection.xlsx")
                .build();
    }

    @POST
    @Path("/upload")
    public Response uploadProjectionOverride(@FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails,
            Map<String, Object> params) throws IOException, InvalidFormatException {

        return Response.ok().build();

    }

}
