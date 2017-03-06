package fk.retail.ip.ssl.internal.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import fk.retail.ip.ssl.config.SslClientConfiguration;
import fk.sp.common.extensions.hystrix.JerseyClientBase;

import javax.ws.rs.client.Client;
/**
 * Created by yogeshwari.k on 01/03/17.
 */
abstract class BaseSslCommand<T> extends HystrixCommand<T> implements JerseyClientBase{
    private static final int DEFAULT_TIME_OUT = 900000;
    private static final String SSL_SERVICE_GROUP = "ssl";
    protected final Client client;
    protected final SslClientConfiguration configuration;

    BaseSslCommand(Client client, SslClientConfiguration configuration) {
        super(
                Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(SSL_SERVICE_GROUP))
                        .andCommandPropertiesDefaults(HystrixCommandProperties
                                .Setter()
                                .withExecutionTimeoutInMilliseconds(
                                        DEFAULT_TIME_OUT)
                                .withRequestLogEnabled(true)
                                .withExecutionTimeoutEnabled(false))
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties
                                .Setter()
                                .withCoreSize(30)));
        this.client = client;
        this.configuration = configuration;
    }
}
