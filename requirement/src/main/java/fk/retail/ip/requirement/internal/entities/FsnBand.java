package fk.retail.ip.requirement.internal.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pragalathan M
 */
@Entity
@Table(name = "FDP_FSN_BAND")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Immutable
public class FsnBand extends ReadOnlyEntity {

    private String fsn;

    private int salesBand;

    private int pvBand;

    private String timeFrame;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FsnBand)) {
            return false;
        }
        FsnBand other = (FsnBand) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.flipkart.ip.db.entity.FsnBand[ id=" + id + " ]";
    }
}
