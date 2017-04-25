package fk.retail.ip.requirement.model;

import com.google.common.collect.Sets;

import java.util.Set;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

@Data
@JsonSnakeCase
public class TriggerRequirementRequest {

    private Set<String> fsns = Sets.newHashSet();
    private Set<Long> groupIds = Sets.newHashSet();
}
