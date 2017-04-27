package fk.retail.ip.requirement.resource;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.proc.model.PushToProcResponse;
import fk.retail.ip.requirement.model.CalculateRequirementRequest;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RaisePORequest;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import fk.retail.ip.requirement.model.RequirementSearchRequest;
import fk.retail.ip.requirement.model.SearchResponse;
import fk.retail.ip.requirement.model.TriggerRequirementRequest;
import fk.retail.ip.requirement.model.UploadResponse;
import fk.retail.ip.requirement.service.RequirementService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Transactional
@Path("/v1/requirement")
@Slf4j
public class RequirementResource {

    private final RequirementService requirementService;

    @Inject
    public RequirementResource(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @Timed(name="batchTriggerReqTimer")
    @Metered(name="batchTriggerReqMeter")
    @ExceptionMetered(name="batchTriggerReqExceptionMeter")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchTriggerRequirement(@Valid TriggerRequirementRequest triggerRequirementRequest) {
        List<String> fsns = requirementService.triggerRequirement(triggerRequirementRequest);
        return Response.ok().entity(fsns).build();
    }

    @Timed(name="calcReqTimer")
    @Metered(name="calcReqMeter")
    @ExceptionMetered(name="calcReqExceptionMeter")
    @POST
    public Response calculateRequirement(@Valid CalculateRequirementRequest calculateRequirementRequest) {
        requirementService.calculateRequirement(calculateRequirementRequest);
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/download")
    @Timed(name="downloadTimer")
    @Metered(name="downloadMeter")
    @ExceptionMetered(name="downloadExceptionMeter")
    public Response download(DownloadRequirementRequest downloadRequirementRequest) {
        log.info("Download Requirement request received " + downloadRequirementRequest);
        StreamingOutput stream = requirementService.downloadRequirement(downloadRequirementRequest);
        return Response.ok(stream)
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = projection.xlsx")
                    .build();

    }
    @Timed(name="uploadTimer")
    @Metered(name="uploadMeter")
    @ExceptionMetered(name="uploadExceptionMeter")
    @POST
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadProjectionOverride(
            @FormDataParam("datafile") InputStream inputStream,
            @FormDataParam("state") String state,
            @HeaderParam("X-Proxy-User") String userId
    ) {

        log.info("Upload Requirement request received for " + state + " state");
        try {
            if (userId == null) {
                userId = "dummyUser";
            }
            UploadResponse uploadResponse = requirementService.uploadRequirement(inputStream, state, userId);
            log.info("Successfully updated " + uploadResponse.getSuccessfulRowCount() + " records");
            return Response.ok(uploadResponse).build();
        } catch (IOException ioException) {
            log.warn("IO exception occurred", ioException.getStackTrace());
            return Response.status(400).build();
        } catch (InvalidFormatException invalidFormat) {
            log.warn("Invalid format exception", invalidFormat.getStackTrace());
            return Response.status(400).build();
        }
    }

    @PUT
    @Path("/state")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(name="changeStateTimer")
    @Metered(name="changeStateMeter")
    @ExceptionMetered(name="changeStateExceptionMeter")
    public String changeState(RequirementApprovalRequest request, @HeaderParam("X-Proxy-User") String userId) throws JSONException {
        if (userId == null) {
            userId = "dummyUser";
        }
        return requirementService.changeState(request, userId);
    }

    @POST
    @Path("/push_to_proc")
    @Produces(MediaType.APPLICATION_JSON)
    public String pushToProc(RaisePORequest request, @HeaderParam("X-Proxy-User") String userId) throws JSONException {
        return requirementService.pushToProc(request, userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/callback/{req_id}")
    public String updateRequirements(@PathParam("req_id") Long reqId, PushToProcResponse callback) {
        return requirementService.setPurchaseOrderId(reqId, callback);
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResponse.GroupedResponse search(RequirementSearchRequest request) throws JSONException {
        return requirementService.search(request);
    }


}
