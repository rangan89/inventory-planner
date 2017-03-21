package fk.retail.ip.zulu.client;

import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import java.util.Collection;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public interface ZuluClient {
    RetailProductAttributeResponse  getRetailProductAttributes(Collection<String> fsns);

}
