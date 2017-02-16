package fk.retail.ip.requirement.internal.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
@Entity
@XmlRootElement
@Getter
@NoArgsConstructor
@Immutable
@Setter
@Table(name = "REQUIREMENT_SNAPSHOT")
public class RequirementSnapshot extends ReadOnlyEntity {

    private String forecast;

    private int inventoryQty;

    private int qoh;

    private int pendingPoQty;

    private int openReqQty;

    private int iwitIntransitQty;

    private String policy;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Group group;

}
