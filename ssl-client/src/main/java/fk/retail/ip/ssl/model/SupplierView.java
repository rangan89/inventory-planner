package fk.retail.ip.ssl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yogeshwari.k on 02/03/17.
 */

@Getter
@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierView {
    String fullName;
    String name;
    String vendorSiteId;
    int mrp;
    boolean local;
    int supplierQuantity;
    int app;
    int sla;
    boolean isPositiveSupplierQuantity;
    boolean isPublisherAndDistributor;
    boolean isPublisher;
    String vendor_preferred_currency;
    String book_supplier_type;
    String source_id;
    String proc_type;
    boolean is_sor;
    int fulfill_quantity;
    String payment_term;
    int logistics_cost;
    int mov;
    int moq;

    public String getSourceId() {
        return source_id;
    }

    public String getVendorPreferredCurrency() {
        return vendor_preferred_currency;
    }
}
