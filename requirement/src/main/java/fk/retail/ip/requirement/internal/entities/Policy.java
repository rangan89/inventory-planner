package fk.retail.ip.requirement.internal.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Policy extends AbstractEntity {

    String policyType;
    String fsn;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    Group group;
}
