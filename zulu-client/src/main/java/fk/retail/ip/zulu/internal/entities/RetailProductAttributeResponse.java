package fk.retail.ip.zulu.internal.entities;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Data
@Getter
public class RetailProductAttributeResponse {
    List<EntityView> entityViews = Lists.newArrayList();
}
