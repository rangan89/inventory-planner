package fk.retail.ip.requirement.internal.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
