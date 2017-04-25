package fk.retail.ip.manager.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import fk.retail.ip.fdp.config.FdpConfiguration;
import fk.retail.ip.requirement.config.TriggerRequirementConfiguration;
import fk.retail.ip.ssl.config.SslClientConfiguration;
import fk.retail.ip.zulu.config.ZuluConfiguration;
import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ManagerConfiguration extends Configuration implements HasDataSourceFactory {

    private DataSourceFactory dataSource;

    @Valid
    @NotNull
    private RotationManagementConfig rotationManagementConfig;

    @NotNull
    private ZuluConfiguration zuluConfiguration;

    @NotNull
    private SslClientConfiguration sslClientConfiguration;

    @NotNull
    private TriggerRequirementConfiguration triggerRequirementConfigurationl;

    @NotNull
    private FdpConfiguration fdpConfiguration;

    @Valid
    @NotNull
    private JerseyClientConfiguration clientConfiguration;

    @Override
    public DataSourceFactory getDatabaseConfiguration() {
        return dataSource;
    }
}
