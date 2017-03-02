package fk.retail.ip.zulu.config;

import com.google.inject.AbstractModule;
import fk.retail.ip.zulu.client.HystrixZuluClient;
import fk.retail.ip.zulu.client.ZuluClient;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
public class ZuluModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ZuluClient.class).to(HystrixZuluClient.class);
    }
}
