package fk.retail.ip.requirement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by agarwal.vaibhav on 10/02/17.
 */
@Getter
@Setter
public class UploadOverrideFailureLineItem {
    private int rowNumber;
    private String fsn;
    private String warehouse;
    private String failureReason;

}
