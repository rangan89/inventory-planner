package fk.retail.ip.requirement.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by agarwal.vaibhav on 05/03/17.
 */
@Getter
@Setter
public class UploadResponse {
    private List<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems;
    private String status;
    private int successfulRowCount;
}
