package fk.retail.ip.ssl.config;

import com.google.inject.AbstractModule;
import fk.retail.ip.ssl.client.HystrixSslClient;
import fk.retail.ip.ssl.client.SslClient;

/**
 * Created by yogeshwari.k on 01/03/17.
 */
public class SslClientModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SslClient.class).to(HystrixSslClient.class);
    }
}
