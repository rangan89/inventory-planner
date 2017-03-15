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
    Integer mrp;
    boolean local;
    Integer supplierQuantity;
    Integer app;
    Integer sla;
    boolean isPositiveSupplierQuantity;
    boolean isPublisherAndDistributor;
    boolean isPublisher;
    String vendor_preferred_currency;
    String book_supplier_type;
    String source_id;
    String proc_type;
    boolean is_sor;
    Integer fulfill_quantity;
    String payment_term;
    Integer logistics_cost;
    Integer mov;
    Integer moq;

    public String getSourceId() {
        return source_id;
    }

    public String getVendorPreferredCurrency() {
        return vendor_preferred_currency;
    }
}
