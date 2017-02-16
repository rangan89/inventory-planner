package fk.retail.ip.manager;

import com.google.common.collect.Sets;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import fk.retail.ip.manager.config.ManagerConfiguration;
import fk.retail.ip.manager.config.ManagerModule;
import fk.retail.ip.requirement.config.RequirementModule;
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
import java.util.Properties;

public class ManagerApplication extends Application<ManagerConfiguration> {

    private GuiceBundle<ManagerConfiguration> guiceBundle;

    @Override
    public void initialize(Bootstrap<ManagerConfiguration> bootstrap) {
        super.initialize(bootstrap);

        this.guiceBundle = GuiceBundle.<ManagerConfiguration>newBuilder()
                .setConfigClass(ManagerConfiguration.class)
                .addModule(new ManagerModule())
                .addModule(new RequirementModule())
                .addModule(new JpaWithSpringModule(
                        Sets.newHashSet(
                                "fk.retail.ip.requirement.internal.entities","fk.retail.ip.requirement.sql"
                        ), new Properties()))
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

    }

    public static void main(String[] args) throws Exception {
        ManagerApplication managerApplication = new ManagerApplication();
        managerApplication.run(args);
    }
}
