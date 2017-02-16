package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Date;

/**
 * Created by yogeshwari.k on 12/02/17.
 */
@Entity
@Table(name = "LAST_APP_SUPPLIER")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@Immutable
public class LastAppSupplier extends ReadOnlyEntity{

    private String fsn;

    private String warehouseId;

    private String lastSupplier;

    private Integer lastApp;


}
