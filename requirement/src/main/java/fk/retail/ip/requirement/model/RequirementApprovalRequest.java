package fk.retail.ip.requirement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementApprovalRequest {

    @JsonProperty("projections")
    private Map<String, Object> filters;
}
