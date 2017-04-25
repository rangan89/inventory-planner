package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
@Data
@Entity
//todo:cleanup
@Table(name = "ip_groups")
public class Group extends ReadOnlyEntity {

    @Column(name = "group_name")
    private String name;

    @Column(name = "is_enabled")
    private boolean enabled;
}
