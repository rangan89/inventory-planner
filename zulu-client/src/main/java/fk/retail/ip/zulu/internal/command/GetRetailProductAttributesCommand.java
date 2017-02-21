package fk.retail.ip.zulu.internal.command;

import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import fk.sp.common.extensions.dropwizard.jersey.NoAuthClient;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public class GetRetailProductAttributesCommand extends BaseZuluCommand<RetailProductAttributeResponse> {

    private final String viewName = "retail_product_attributes";
    private List<String> fsns;

    @Inject
    GetRetailProductAttributesCommand(@NoAuthClient Client client, ZuluConfiguration configuration) {
        super(client,configuration);
    }

    @Override
    protected RetailProductAttributeResponse run() throws Exception {
        URI uri = UriBuilder
                .fromUri(configuration.getUrl())
                .path(configuration.getViewPath())
                .queryParam("viewNames", viewName)
                .queryParam("entityIds", StringUtils.join(fsns, ','))
                .build();

        long timestamp = System.currentTimeMillis();
        RetailProductAttributeResponse response = client.target(uri)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .header("z-clientId", configuration.getClientId())
                .header("z-requestId", timestamp)
                .header("z-timestamp", timestamp)
                .get(RetailProductAttributeResponse.class);

        return response;
    }

    public GetRetailProductAttributesCommand withFsns(List<String> fsns) {
        this.fsns = fsns;
        return this;
    }

}
