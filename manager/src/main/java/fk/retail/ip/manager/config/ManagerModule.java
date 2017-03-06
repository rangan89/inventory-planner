package fk.retail.ip.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import fk.retail.ip.ssl.config.SslClientConfiguration;
import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import io.dropwizard.client.JerseyClientConfiguration;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import org.glassfish.jersey.filter.LoggingFilter;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class ManagerModule extends AbstractModule {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void configure() {
        bind(HasDataSourceFactory.class).to(ManagerConfiguration.class);
        Multibinder<ClientResponseFilter>
                clientResponseFilterMultibinder =
                newSetBinder(binder(), new TypeLiteral<ClientResponseFilter>() {
                });
        clientResponseFilterMultibinder.addBinding().toInstance(new LoggingFilter(
                Logger.getLogger(LoggingFilter.class.getName()), true));

        Multibinder<ClientRequestFilter>
                ClientRequestFilterMultibinder =
                newSetBinder(binder(), new TypeLiteral<ClientRequestFilter>() {
                });
        ClientRequestFilterMultibinder.addBinding().toInstance(new LoggingFilter(
                Logger.getLogger(LoggingFilter.class.getName()), true));
    }

    @Provides
    public ZuluConfiguration getZuluConfiguration(ManagerConfiguration managerConfiguration) {
        return managerConfiguration.getZuluConfiguration();
    }

    @Provides
    public SslClientConfiguration getSslClientConfiguration(ManagerConfiguration managerConfiguration) {
        return  managerConfiguration.getSslClientConfiguration();
    }

    @Provides
    public JerseyClientConfiguration getJerseyClientConfiguration(
            ManagerConfiguration managerConfiguration) {
        return managerConfiguration.getClientConfiguration();
    }

    @Provides
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
