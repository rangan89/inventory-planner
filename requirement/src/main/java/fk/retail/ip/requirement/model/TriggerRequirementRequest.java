package fk.retail.ip.requirement.model;

import com.google.common.collect.Sets;

import java.util.Set;

import lombok.Data;

@Data
public class TriggerRequirementRequest {

    private Set<String> fsns = Sets.newHashSet();
    private Set<Long> groupIds = Sets.newHashSet();
}
