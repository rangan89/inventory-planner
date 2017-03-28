package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import java.util.List;
import java.util.Map;

/**
 * Created by nidhigupta.m on 22/03/17.
 */

public class SearchFilterCommand {

    private final ProductInfoRepository productInfoRepository;
    private final GroupFsnRepository groupFsnRepository;

    @Inject
    public SearchFilterCommand(ProductInfoRepository productInfoRepository, GroupFsnRepository groupFsnRepository) {
        this.productInfoRepository = productInfoRepository;
        this.groupFsnRepository = groupFsnRepository;
    }

    public List<String> getSearchFilterFsns(Map<String, Object> filters) {

        List<String> allFsns = groupFsnRepository.getAllFsns();
        List<String> fsns = (List<String>) filters.get("fsns");
        getFsnsIntersection(allFsns,fsns);
        List<String> productInfoFsns = getProductInfoFsns(filters);
        getFsnsIntersection(allFsns,productInfoFsns);
        List<String> groupFsns = getGroupFsns(filters);
        getFsnsIntersection(allFsns, groupFsns);
        return allFsns;
    }

    private List<String> getProductInfoFsns(Map<String, Object> filters) {
        String vertical = filters.containsKey("vertical")? filters.get("vertical").toString():null;
        String category = filters.containsKey("category")? filters.get("category").toString():null;
        String superCategory = filters.containsKey("superCategory")? filters.get("superCategory").toString():null;
        String businessUnit = filters.containsKey("businessUnit")? filters.get("businessUnit").toString():null;
        return productInfoRepository.getFsns(vertical, category, superCategory, businessUnit);
    }

    private  List<String> getGroupFsns(Map<String, Object> filters) {

        //todo: change when group comes as a string, As of now it comes as a list from UI
        List<String> group = (List<String>) filters.get("group");
        List<String> groupFsns = Lists.newArrayList();
        if (group != null && !group.isEmpty()) {
            groupFsns.addAll(groupFsnRepository.getFsns(group.get(0).toString()));
        }
        return  groupFsns;
    }

    private void getFsnsIntersection(List<String> fsns, List<String> otherFsns) {
        if (otherFsns != null && !otherFsns.isEmpty()) {
                fsns.retainAll(otherFsns);
        }
    }

}
