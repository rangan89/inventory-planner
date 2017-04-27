package fk.retail.ip.proc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import fk.retail.ip.proc.model.PushToProcRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by yogeshwari.k on 07/04/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushToProcRequestWrapper {

    @JsonProperty("requirements")
    List<PushToProcRequest> pushToProcRequestList = Lists.newArrayList();

}
