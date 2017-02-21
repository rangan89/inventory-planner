package fk.retail.ip.zulu.client;

import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;

import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public interface ZuluClient {
    RetailProductAttributeResponse  getRetailProductAttributes(List<String> fsns);
}
