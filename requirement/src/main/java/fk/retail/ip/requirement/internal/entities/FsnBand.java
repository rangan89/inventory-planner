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
@Table(name = "FDP_FSN_BAND")
@XmlRootElement
public class FsnBand extends ReadOnlyEntity {

    private String fsn;
    private int salesBand;
    private int pvBand;
    private String timeFrame;
}
