package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fk.retail.ip.requirement.config.TestModule;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by nidhigupta.m on 23/03/17.
 */


@RunWith(JukitoRunner.class)
@UseModules(TestModule.class)


public class SearchFilterCommandTest {

    @InjectMocks
    SearchFilterCommand searchFilterCommand;

    @Mock
    ProductInfoRepository productInfoRepository;

    @Mock
    GroupFsnRepository groupFsnRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetSearchFilterFsns() {
        List<String> allFsns = Lists.newArrayList("fsns1", "fsn2", "fsn3", "fsn4", "fsn5");
        Mockito.when(groupFsnRepository.getAllFsns()).thenReturn(allFsns);
        Mockito.when(productInfoRepository.getFsns(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Arrays.asList("fsns1", "fsn2", "fsn3"));
        Mockito.when(groupFsnRepository.getFsns(Mockito.anyString())).thenReturn(Arrays.asList("fsn1", "fsn2", "fsn4"));
        Map<String, Object> filters = Maps.newHashMap();
        filters.put("fsns", Arrays.asList("fsn2"));
        filters.put("vertical", "vertical");
        filters.put("group",Arrays.asList("group"));
        List<String> fsns = searchFilterCommand.getSearchFilterFsns(filters);
        Assert.assertEquals(1,fsns.size());
        Assert.assertEquals("fsn2", fsns.get(0));
    }

}
