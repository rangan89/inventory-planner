package fk.retail.ip.requirement.internal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@MappedSuperclass
@Data
public class ReadOnlyEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @PrePersist
    private void beforePersist() {
        createdAt = new Date();
    }
}
