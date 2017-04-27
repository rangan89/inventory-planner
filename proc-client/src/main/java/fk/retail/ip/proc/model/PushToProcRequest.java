package fk.retail.ip.proc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by yogeshwari.k on 07/04/17.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class PushToProcRequest {
    private String warehouseId;
    private String fsn;
    private String sku;
    private Boolean local;
    private String sourceId;
    private int quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date requiredByDate;
    private String requirementType;
    private Float supplierApp;
    private Float supplierMrp;
    private String currency;
}
