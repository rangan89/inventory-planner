package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
@Data
@Entity
public class Group extends AbstractEntity {

    private String name;

    @Column(name = "proc_type")
    private String procType;

    @Column(name = "is_enabled")
    private boolean isEnabled;

}
