package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * @author Pragalathan M
 */
@Entity
@Table(name = "FORECAST")
@XmlRootElement
@Getter
@NoArgsConstructor
@Immutable
public class FsnForecast extends ReadOnlyEntity {

    private String fsn;
    private String warehouse;
    private String forecast;
}
