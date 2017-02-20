package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Pragalathan M
 */

@Entity
//todo:cleanup
//@Table(name = "FDP_FSN_SALES_BUCKET")
@Table(name = "fsn_week_sales_bucket_data")
@XmlRootElement
@Data
public class WeeklySale extends ReadOnlyEntity {

    private String fsn;

    private String warehouse;

    private Integer week;

    //todo:cleanup
    @Column(name = "sale_unit")
    private Integer saleQty;



}
