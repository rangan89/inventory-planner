package fk.retail.ip.requirement.internal.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ip_group_fsns")
public class GroupFsn extends ReadOnlyEntity {

    String fsn;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_id")
    Group group;
}
