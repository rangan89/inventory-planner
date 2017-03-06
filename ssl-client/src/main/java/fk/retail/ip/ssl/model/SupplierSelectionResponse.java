package fk.retail.ip.ssl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Created by yogeshwari.k on 02/03/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierSelectionResponse {
    boolean local;
    List<SupplierView> suppliers;
    List<SupplierView> otherSuppliers;
}
