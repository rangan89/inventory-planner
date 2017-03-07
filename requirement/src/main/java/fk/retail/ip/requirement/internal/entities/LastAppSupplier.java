package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;


/**
 * Created by yogeshwari.k on 12/02/17.
 */
@Entity
@Table(name = "last_app_supplier")
@XmlRootElement
@Data

public class LastAppSupplier extends ReadOnlyEntity {

    private String fsn;

    private String warehouse;

    private String lastSupplier;

    private Integer lastApp;

}
