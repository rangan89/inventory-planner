package fk.retail.ip.ssl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.jackson.JsonSnakeCase;
import java.util.List;
import lombok.Data;

/**
 * Created by yogeshwari.k on 02/03/17.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierSelectionResponse {
    boolean local;
    String fsn;
    String sku;
    String category;
    int quantity;
    String entityType;
    long entityId;
    String warehouseId;
    String orderType;
    String tenantId;
    List<SupplierView> suppliers;
    List<SupplierView> otherSuppliers;
}
