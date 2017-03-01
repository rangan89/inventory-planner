package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author Pragalathan M
 */
@Data
@Entity
//todo:cleanup
//@Table(name = "FORECAST")
@Table(name = "forecast_regional")
public class Forecast extends ReadOnlyEntity {

    private String fsn;
    private String warehouse;
    private String forecast;
}
