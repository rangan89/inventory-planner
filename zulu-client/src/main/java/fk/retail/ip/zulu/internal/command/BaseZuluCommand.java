package fk.retail.ip.zulu.internal.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.sp.common.extensions.hystrix.JerseyClientBase;

import javax.ws.rs.client.Client;

/**
 * Created by nidhigupta.m on 03/02/17.
 */
abstract class BaseZuluCommand<T> extends HystrixCommand<T> implements JerseyClientBase {
    private static final int DEFAULT_TIME_OUT = 900000;
    private static final String ZULU_SERVICE_GROUP = "zulu";
    protected final Client client;
    protected final ZuluConfiguration configuration;

    BaseZuluCommand(Client client, ZuluConfiguration configuration) {
        super(
                Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ZULU_SERVICE_GROUP))
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
