package fk.retail.ip.ssl.client;

import fk.retail.ip.ssl.internal.command.GetSupplierDetailsCommand;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;

import javax.inject.Provider;
import com.google.inject.Inject;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;

import java.util.List;

/**
 * Created by yogeshwari.k on 01/03/17.
 */
public class HystrixSslClient implements SslClient{

    private final Provider<GetSupplierDetailsCommand> getSupplierDetailsCommandProvider;

    @Inject
    public HystrixSslClient(Provider<GetSupplierDetailsCommand> getSupplierDetailsCommandProvider) {
        this.getSupplierDetailsCommandProvider = getSupplierDetailsCommandProvider;
    }

    @Override
    public List<SupplierSelectionResponse> getSupplierSelectionResponse(List<SupplierSelectionRequest> requests) {
        return getSupplierDetailsCommandProvider.get()
                .withSslRequests(requests)
                .execute();
    }

}
