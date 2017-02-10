package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.config.TestModule;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class RequirementRepositoryTest extends TransactionalJpaRepositoryTest {

  @Inject
  RequirementRepository requirementRepository;

  @Test
  public void testFindRequirementByIds() {
    Requirement requirement1 = new Requirement();
    requirement1.setFsn("fsn1");
    requirement1.setState("proposed");
    requirement1.setWarehouse("dummy_warehouse");
    requirement1.setCreatedAt(new Date());
    requirement1.setUpdatedAt(new Date());
    requirementRepository.persist(requirement1);
    Requirement requirement2 = new Requirement();
    requirement2.setFsn("fsn2");
    requirement2.setState("proposed");
    requirement2.setWarehouse("dummy_warehouse");
    requirement2.setCreatedAt(new Date());
    requirement2.setUpdatedAt(new Date());
    requirementRepository.persist(requirement2);
    List<Requirement>
        requirements = requirementRepository.findRequirementByIds(Lists.newArrayList(new Long(1)));
    Assert.assertEquals(1, requirements.size());
    Assert.assertEquals(requirement1, requirements.get(0));
  }

}
