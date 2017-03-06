package fk.retail.ip.manager;

import com.codahale.metrics.JmxReporter;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.google.common.collect.Sets;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import fk.retail.ip.manager.config.ManagerConfiguration;
import fk.retail.ip.manager.config.ManagerModule;
import fk.retail.ip.requirement.config.RequirementModule;
import fk.retail.ip.ssl.config.SslClientModule;
import fk.retail.ip.zulu.config.ZuluModule;
import fk.sp.common.extensions.RequestContextFilter;
import fk.sp.common.extensions.config.CustomEnumModule;
import fk.sp.common.extensions.dropwizard.jersey.JerseyClientModule;
import fk.sp.common.extensions.dropwizard.jersey.LoggingFilter;
import fk.sp.common.extensions.guice.jpa.spring.JpaWithSpringModule;
import flipkart.retail.server.admin.bundle.RotationManagementBundle;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;
import javax.servlet.DispatcherType;

public class ManagerApplication extends Application<ManagerConfiguration> {

    private GuiceBundle<ManagerConfiguration> guiceBundle;

    @Override
    public void initialize(Bootstrap<ManagerConfiguration> bootstrap) {
        super.initialize(bootstrap);
        Properties jpaProperties = new Properties();
        jpaProperties.put(JpaWithSpringModule.HIBERNATE_EJB_NAMING_STRATEGY,
                "fk.retail.ip.manager.config.AnnotationRespectfulNamingStrategy");
        this.guiceBundle = GuiceBundle.<ManagerConfiguration>newBuilder()
                .setConfigClass(ManagerConfiguration.class)
                .addModule(new ManagerModule())
                .addModule(new JerseyClientModule())
                .addModule(new RequirementModule())
                .addModule(new ZuluModule())
                .addModule(new SslClientModule())
                .addModule(new JpaWithSpringModule(
                        Sets.newHashSet(
                                "fk.retail.ip"
                        ), jpaProperties))
                .enableAutoConfig(
                        "fk.sp.common.extensions.exception",
                        "fk.sp.common.extensions.dropwizard.hystrix",
                        "fk.sp.common.extensions.dropwizard.logging",
                        "fk.sp.common.extensions.dropwizard.jmx",
                        "fk.sp.common.extensions.dropwizard.jpa",
                        "fk.sp.common.extensions.filter",
                        "fk.retail.ip"
                )
                .build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new MultiPartBundle());

        //For oor and bir task and healthcheck integration
        bootstrap.addBundle(new RotationManagementBundle<ManagerConfiguration>() {
            @Override
            public RotationManagementConfig getRotationManagementConfig(
                    ManagerConfiguration managerConfiguration) {
                return managerConfiguration.getRotationManagementConfig();
            }
        });

        //For dbMigrations support
        bootstrap.addBundle(new MigrationsBundle<ManagerConfiguration>() {
            public DataSourceFactory getDataSourceFactory(
                    ManagerConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });

    }

    @Override
    public void run(ManagerConfiguration configuration, Environment environment) throws Exception {

        //For swagger UI
        AssetsBundle assetsBundle = new AssetsBundle("/apidocs", "/apidocs", "index.html", "/apidocs");
        assetsBundle.run(environment);
        environment.jersey().register(LoggingFilter.class);

        environment.getObjectMapper().registerModule(new Jdk7Module());
        environment.getObjectMapper().setTimeZone(TimeZone.getTimeZone("UTC"));
        environment.getObjectMapper().registerModule(new CustomEnumModule());

        environment.servlets().addFilter("request context filter",
                new RequestContextFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        JmxReporter reporter = JmxReporter.forRegistry(environment.metrics()).build();
        reporter.start();

    }

    public static void main(String[] args) throws Exception {
        ManagerApplication managerApplication = new ManagerApplication();
        managerApplication.run(args);

    }
}
