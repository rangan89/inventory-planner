package fk.retail.ip.requirement.model;

import com.google.common.collect.Sets;
import io.dropwizard.jackson.JsonSnakeCase;
import java.util.Set;
import lombok.Data;

@Data
@JsonSnakeCase
public class CalculateRequirementRequest {
    Set<String> fsns = Sets.newHashSet();
}
