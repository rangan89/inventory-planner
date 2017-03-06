package fk.retail.ip.ssl.model;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Date;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by yogeshwari.k on 01/03/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonSnakeCase
public class SupplierSelectionRequest {

    private String fsn;
    private String sku;
    private String requiredByDate;
    private String orderType;
    private int quantity;
    private String entityType;
    private String warehouseId;
    private String tenantId;

}
