package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.apache.commons.lang.ArrayUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)
public class RequirementRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    RequirementRepository requirementRepository;

    @After
    public void resetAutoIncrement() {
        entityManagerProvider.get()
                .createNativeQuery("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK")
                .executeUpdate();
    }

    @Test
    public void testFindRequirementById() {
        Requirement requirement = getRequirement(1);
        requirementRepository.persist(requirement);
        List<Requirement>
                requirements = requirementRepository.findRequirementByIds(Lists.newArrayList(new Long(1)));
        Assert.assertEquals(1, requirements.size());
        Assert.assertEquals(requirement, requirements.get(0));
    }

    @Test
    public void testFindRequirementByIdArray() {

        IntStream.rangeClosed(1, 30).forEach(i -> {

            requirementRepository.persist(getRequirement(i));
        });

        long[] ids = LongStream.rangeClosed(1, 30).toArray();
        Long[] idList = ArrayUtils.toObject(ids);
        List<Long> idsAsList = Arrays.asList(idList);

        List<Requirement>
                requirements = requirementRepository.findRequirementByIds(idsAsList);
        Assert.assertEquals(30, requirements.size());
    }

    @Test
    public void testFindAllEnabledRequirements() {

        IntStream.rangeClosed(1, 24).forEach(i -> {
            requirementRepository.persist(getRequirement(i));
        });
        List<Requirement>
                requirements = requirementRepository.findAllEnabledRequirements("proposed");
        Assert.assertEquals(24, requirements.size());
    }

    private Requirement getRequirement(int i) {
        Requirement requirement = new Requirement();
        requirement.setFsn("fsn" + String.valueOf(i));
        requirement.setState("proposed");
        requirement.setEnabled(true);
        requirement.setWarehouse("dummy_warehouse");
        requirement.setCreatedAt(new Date());
        requirement.setUpdatedAt(new Date());
        return requirement;
    }


}
