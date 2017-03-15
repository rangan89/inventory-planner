
package fk.retail.ip.requirement.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Singleton
public class TestModule extends AbstractModule {

  @Override
  protected void configure() {

    install(new RequirementModule());
  }

}
