package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by ashishkumar.p on 22/03/17.
 */
@Data
@NoArgsConstructor
@JsonSnakeCase
public class SearchResponse {
    @JsonProperty("id")
    Long projectionId;
    Long groupId;
    boolean enabled;
    String fsn;
    String currentState;
    int intransit;
    int inventory;
    List<RequirementSearchLineItem> projectionStates = Lists.newArrayList();
    ProductInfo productInfo = new ProductInfo();
    List<FsnBand> fsnBands = Lists.newArrayList();
    public SearchResponse(List<RequirementSearchLineItem> requirementSearchLineItems) {
        //set top level
        FsnBand fsnBand = new FsnBand();
        projectionId = requirementSearchLineItems.get(0).getProjectionId();
        groupId = requirementSearchLineItems.get(0).getGroupId();
        enabled = requirementSearchLineItems.get(0).isEnabled();
        fsn = requirementSearchLineItems.get(0).getFsn();
        currentState = requirementSearchLineItems.get(0).getState();
        requirementSearchLineItems.forEach(requirementSearchLineItem -> {
            intransit += requirementSearchLineItem.getIntransitQty();
            inventory += requirementSearchLineItem.getInventory();
        });
        projectionStates = Lists.newArrayList(requirementSearchLineItems);
        productInfo.setTitle(requirementSearchLineItems.get(0).getTitle());
        productInfo.setAnalytic_vertical(requirementSearchLineItems.get(0).getVertical());
        productInfo.setCategory(requirementSearchLineItems.get(0).getCategory());
        productInfo.setSuperCategory(requirementSearchLineItems.get(0).getSuperCategory());
        fsnBand.setPvBand(requirementSearchLineItems.get(0).getPvBand());
        fsnBand.setSalesBand(requirementSearchLineItems.get(0).getSalesBand());
        fsnBands.add(fsnBand);
    }

    @Data
    @JsonSnakeCase
    @NoArgsConstructor
    public static class GroupedResponse {
        List<SearchResponse> projections = Lists.newArrayList();
        long totalEntries;
        int perPage;
        public GroupedResponse(long totalEntries, int perPage) {
            this.totalEntries = totalEntries;
            this.perPage = perPage;
        }
    }

    @Data
    @JsonSnakeCase
    public static class ProductInfo {
        String title;
        String analytic_vertical;
        String category;
        String superCategory;
    }

    @Data
    @JsonSnakeCase
    public static class FsnBand {
        Integer pvBand;
        Integer salesBand;
    }
}
