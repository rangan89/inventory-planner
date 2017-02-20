package fk.retail.ip.requirement.internal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date createdAt = new Date();


    @Temporal(TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    protected Date updatedAt = new Date();

    @NotNull
    @Version
    private long version;

    @PreUpdate
    public void setUpdate() {  this.updatedAt = new Date(); }

}
