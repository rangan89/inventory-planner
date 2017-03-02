package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import io.dropwizard.jackson.JsonSnakeCase;
import java.util.Set;
import lombok.Data;

@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculateRequirementRequest {
    @JsonProperty(value = "fsn_list")
    Set<String> fsns = Sets.newHashSet();
}
