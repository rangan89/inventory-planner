package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Entity
//todo:change this table name
@Table(name = "product_detail")
@XmlRootElement
@Data
@NoArgsConstructor
public class ProductInfo extends AbstractEntity {
    @NotNull
    private String fsn;
    private String vertical;
    private String category;
    private String superCategory;
    private String title;
    private String brand;
    private Integer fsp;
}
