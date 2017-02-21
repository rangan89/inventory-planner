package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**

 * @author Pragalathan M
 */
@Data
@Entity
//todo:cleanup
//@Table(name = "FDP_FSN_BAND")
@Table(name = "fsn_bands")
@XmlRootElement
public class FsnBand extends ReadOnlyEntity {

    private String fsn;
    private Integer salesBand;
    private Integer pvBand;
    private String timeFrame;
}
