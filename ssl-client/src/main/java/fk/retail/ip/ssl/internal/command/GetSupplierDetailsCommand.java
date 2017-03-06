package fk.retail.ip.ssl.internal.command;

import com.google.common.collect.Lists;
import fk.retail.ip.ssl.config.SslClientConfiguration;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import fk.sp.common.extensions.dropwizard.jersey.NoAuthClient;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;
import java.io.ByteArrayOutputStream;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Created by yogeshwari.k on 02/03/17.
 */
@Slf4j
public class GetSupplierDetailsCommand extends BaseSslCommand<List<SupplierSelectionResponse>>{

    List<SupplierSelectionRequest> request;

    @Inject
    GetSupplierDetailsCommand(@NoAuthClient Client client, SslClientConfiguration configuration) {
        super(client,configuration);
    }

    @Override
    protected List<SupplierSelectionResponse> run() throws Exception {
        URI uri = UriBuilder
                .fromUri(configuration.getUrl())
                .path(configuration.getViewPath())
                .build();

        Response response = client.target(uri)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(serializeToByteArray(request)));
        if (response.getStatus() != 200) {
            List<SupplierSelectionResponse> emptyResult = Lists.newArrayList();
            return emptyResult;
        }
        List<SupplierSelectionResponse> result = response.readEntity(new GenericType<List<SupplierSelectionResponse>>(){});
        return result;
    }

    private byte[] serializeToByteArray(Object entity) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Jackson.newObjectMapper().writeValue(byteArrayOutputStream, entity);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error serializing entity", e);
            throw new RuntimeException(e);
        }
    }

    public GetSupplierDetailsCommand withSslRequests(List<SupplierSelectionRequest> request) {
        this.request = request;
        return this;
    }
}
