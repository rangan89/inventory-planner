package fk.retail.ip.requirement.internal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@MappedSuperclass
@Getter
@Setter
public class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;

    @NotNull
    @Version
    private long version;

}
