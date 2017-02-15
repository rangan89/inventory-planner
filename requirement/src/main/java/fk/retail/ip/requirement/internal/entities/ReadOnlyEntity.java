package fk.retail.ip.requirement.internal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@MappedSuperclass
@Getter
@Setter
@Immutable
public class ReadOnlyEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

}
