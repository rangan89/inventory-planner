package fk.retail.ip.zulu.client;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.retail.ip.zulu.internal.command.GetRetailProductAttributesCommand;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import java.util.Collection;
import javax.inject.Provider;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public class HystrixZuluClient implements ZuluClient {

    private final Provider<GetRetailProductAttributesCommand> getRetailProductAttributesCommand;
    private final ZuluConfiguration zuluConfiguration;

    @Inject
    public HystrixZuluClient(Provider<GetRetailProductAttributesCommand> getRetailProductAttributesCommand, ZuluConfiguration zuluConfiguration) {
        this.getRetailProductAttributesCommand = getRetailProductAttributesCommand;
        this.zuluConfiguration = zuluConfiguration;
    }


    @Override
    public RetailProductAttributeResponse getRetailProductAttributes(Collection<String> fsns) {
        RetailProductAttributeResponse response = new RetailProductAttributeResponse();
        Lists.partition(Lists.newArrayList(fsns), zuluConfiguration.getMaxBatchSize()).forEach(partition -> {
            RetailProductAttributeResponse zuluResponse = getRetailProductAttributesCommand.get().withFsns(partition).execute();
            response.getEntityViews().addAll(zuluResponse.getEntityViews());
        });
        return response;
    }
}
