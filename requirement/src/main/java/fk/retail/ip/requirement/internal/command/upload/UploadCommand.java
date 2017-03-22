package fk.retail.ip.requirement.internal.command.upload;


import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by vaibhav.agarwal on 03/02/17.
 * Code never lies.
*/
@Slf4j
public abstract class UploadCommand {

    abstract Map<String, Object> validateAndSetStateSpecificFields(RequirementDownloadLineItem row);

    private final RequirementRepository requirementRepository;

    public UploadCommand(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    public List<UploadOverrideFailureLineItem> execute(
            List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements
    ) {

        Map<Long, Requirement> requirementMap = requirements.stream().
                collect(Collectors.toMap(Requirement::getId, Function.identity()));

        ArrayList<UploadOverrideFailureLineItem> uploadOverrideFailureLineItems = new ArrayList<>();
        int rowCount = 0;
        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            UploadOverrideFailureLineItem uploadOverrideFailureLineItem = new UploadOverrideFailureLineItem();
            rowCount += 1;
            String fsn = row.getFsn();
            String warehouse = row.getWarehouseName();

            Optional<String> genericComment = validateGenericColumns(fsn, warehouse);

            if (genericComment.isPresent()) {
                uploadOverrideFailureLineItem.setFailureReason(genericComment.get());
                uploadOverrideFailureLineItem.setFsn(fsn == null ? "" : fsn);
                uploadOverrideFailureLineItem.setWarehouse(warehouse == null ? "" : warehouse);
                uploadOverrideFailureLineItem.setRowNumber(rowCount);
                uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);

            } else {

                Map<String, Object> overriddenValues = validateAndSetStateSpecificFields(row);
                String status = overriddenValues.get(Constants.STATUS).toString();
                OverrideStatus overrideStatus = OverrideStatus.fromString(status);

                switch(overrideStatus) {
                    case FAILURE:
                        uploadOverrideFailureLineItem.setFailureReason(overriddenValues.get
                                (OverrideKey.OVERRIDE_COMMENT.toString()).toString());
                        uploadOverrideFailureLineItem.setFsn(fsn);
                        uploadOverrideFailureLineItem.setRowNumber(rowCount);
                        uploadOverrideFailureLineItem.setWarehouse(warehouse);
                        uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);
                        break;

                    case UPDATE:
                        Long requirementId = row.getRequirementId();

                        if (requirementMap.containsKey(requirementId)) {
                            Requirement requirement = requirementMap.get(requirementId);

                            if (overriddenValues.containsKey(OverrideKey.QUANTITY.toString())) {
                                requirement.setQuantity
                                        ((Integer) overriddenValues.get(OverrideKey.QUANTITY.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.SLA.toString())) {
                                requirement.setSla((Integer) overriddenValues.get(OverrideKey.SLA.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.APP.toString())) {
                                requirement.setApp((Integer) overriddenValues.get(OverrideKey.APP.toString()));
                            }

                            if (overriddenValues.containsKey(OverrideKey.SUPPLIER.toString())) {
                                requirement.setSupplier
                                        (overriddenValues.get(OverrideKey.SUPPLIER.toString()).toString());
                            }

                            if (overriddenValues.containsKey(OverrideKey.OVERRIDE_COMMENT.toString())) {
                                requirement.setOverrideComment
                                        (overriddenValues.get(OverrideKey.OVERRIDE_COMMENT.toString()).toString());
                            }

                        } else {
                            uploadOverrideFailureLineItem.setFailureReason
                                    (Constants.REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID);
                            uploadOverrideFailureLineItem.setFsn(fsn);
                            uploadOverrideFailureLineItem.setRowNumber(rowCount);
                            uploadOverrideFailureLineItem.setWarehouse(warehouse);
                            uploadOverrideFailureLineItems.add(uploadOverrideFailureLineItem);
                        }
                        break;

                    case SUCCESS:
                        break;

                    }

                }
            }

        return uploadOverrideFailureLineItems;
    }

    private Optional<String> validateGenericColumns(String fsn, String warehouse){
        String genericValidationComment;
        if (fsn == null || warehouse == null) {
           genericValidationComment =  Constants.FSN_OR_WAREHOUSE_IS_MISSING;
            return Optional.of(genericValidationComment);
        }
        return Optional.empty();
    }

    protected Optional<String> validateQuantityOverride(
            Integer currentQuantity, Integer suggestedQuantity, String overrideComment) {
        String validationComment;
        if (suggestedQuantity == null) {
            return Optional.empty();
        }
        if (suggestedQuantity < 0) {
            validationComment = isEmptyString(overrideComment) ?
                    Constants.INVALID_QUANTITY_WITHOUT_COMMENT :
                    Constants.SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO;
            return Optional.of(validationComment);
        } else if (suggestedQuantity != currentQuantity && isEmptyString(overrideComment)) {
            validationComment = Constants.QUANTITY_OVERRIDE_COMMENT_IS_MISSING;
            return Optional.of(validationComment);
        } else {
            return Optional.empty();
        }
    }

    protected boolean isEmptyString(String comment) {
        return comment == null || comment.trim().isEmpty() ? true : false;
    }

}
