
package fk.retail.ip.requirement.config;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fk.sp.common.extensions.jpa.JpaWithTestDbModule;

@Singleton
public class TestDbModule extends AbstractModule {

  @Override
  protected void configure() {

    install(new RequirementModule());
    install(new JpaWithTestDbModule("default", Lists.newArrayList("fk.retail.ip")));
  }

}
