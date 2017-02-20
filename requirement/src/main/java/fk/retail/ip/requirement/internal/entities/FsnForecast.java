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
//@Table(name = "FORECAST")
@Table(name = "forecast_regional")
@XmlRootElement
public class FsnForecast extends ReadOnlyEntity {

    private String fsn;
    private String warehouse;
    private String forecast;
}
