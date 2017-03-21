package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@UseModules(TestDbModule.class)
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
        List<Requirement> requirements = requirementRepository.findRequirementByIds(Lists.newArrayList(new Long(1)));
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

        List<Requirement> requirements = requirementRepository.findRequirementByIds(idsAsList);
        Assert.assertEquals(30, requirements.size());
    }

    @Test
    public void testFindAllCurrentRequirements() {

        IntStream.rangeClosed(1, 24).forEach(i -> {
            requirementRepository.persist(getRequirement(i));
        });
        List<Requirement> requirements = requirementRepository.findAllCurrentRequirements("proposed");
        Assert.assertEquals(24, requirements.size());
    }

    @Test
    public void testFindEnabledRequirementsByStateFsn() {

        Set<String> fsns = new HashSet<String>();
        IntStream.rangeClosed(1, 20).forEach(i -> {
            requirementRepository.persist(getRequirement(i));
            fsns.add("fsn" + String.valueOf(i));
        });
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("proposed", fsns);
        Assert.assertEquals(20, requirements.size());
    }


    @Test
    public void testGetRequirementByFilters() {

        Group grp1 = TestHelper.getGroup("grp1");
        RequirementSnapshot requirementSnapshot = TestHelper.getRequirementSnapshot("",1,1,1,1,1);
        requirementSnapshot.setGroup(grp1);
        Requirement req1 = TestHelper.getRequirement("fsn1", "dummy_warehouse","proposed", true, requirementSnapshot, 10, "supplier",
                10, 110, "INR",2,"comment", "daily"  );
        req1.setCurrent(true);
        req1.setProjectionId(1l);
        req1.setInternational(true);

        Group grp2 = TestHelper.getGroup("grp2");
        RequirementSnapshot requirementSnapshot2 = TestHelper.getRequirementSnapshot("",1,1,1,1,1);
        requirementSnapshot2.setGroup(grp2);
        Requirement req2 = TestHelper.getRequirement("fsn2", "dummy_warehouse","proposed", true, requirementSnapshot2, 10, "supplier",
                10, 11, "INR",2,"comment", "daily"  );
        req2.setCurrent(true);
        req2.setProjectionId(1l);
        requirementRepository.persist(req1);
        requirementRepository.persist(req2);
        Map<String, Object> filtergrp1 = new HashMap<>();

        filtergrp1.put("fsns",Arrays.asList());
        filtergrp1.put("group",Arrays.asList("grp1"));
        filtergrp1.put("price_from","100");
        filtergrp1.put("price_to","120");
        filtergrp1.put("international","1");
        List<Long> requirementIds = Collections.emptyList();
        List<Requirement> requirements = requirementRepository.findRequirements(requirementIds,"proposed",filtergrp1);
        Assert.assertEquals(1, requirements.size());
        Assert.assertEquals(requirements.get(0), req1);
        Assert.assertNotEquals(requirements.get(0), req2);
    }

    private Requirement getRequirement(int i) {
        String fsn = "fsn" + String.valueOf(i);
        Requirement requirement = TestHelper.getRequirement(fsn, "dummy_warehouse", "proposed", true, null, 10, "supplier",
                10, 11, "INR", 2, "comment", "daily");
        requirement.setCurrent(true);

        return requirement;
    }

}
