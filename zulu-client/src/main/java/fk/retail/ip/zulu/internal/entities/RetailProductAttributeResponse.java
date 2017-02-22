package fk.retail.ip.zulu.internal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Data;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetailProductAttributeResponse {
    List<EntityView> entityViews = Lists.newArrayList();
}
