package fk.retail.ip.requirement.internal.command.upload;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.OverrideStatus;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by agarwal.vaibhav on 06/02/17.
 */
@Slf4j
public class BizFinReviewUploadCommand extends UploadCommand {

    @Inject
    public BizFinReviewUploadCommand(RequirementRepository requirementRepository) {
        super(requirementRepository);
    }

    @Override
    Map<String, Object> validateAndSetStateSpecificFields(RequirementDownloadLineItem requirementDownloadLineItem) {

        String quantityOverrideComment = requirementDownloadLineItem.getBizFinComment();
        Integer currentQuantity = requirementDownloadLineItem.getQuantity();
        Integer bizfinProposedQuantity = requirementDownloadLineItem.getBizFinRecommendedQuantity();
        Map<String, Object> overriddenValues = new HashMap<>();
        Optional<String> validationResponse = validateQuantityOverride(
                currentQuantity,
                bizfinProposedQuantity,
                quantityOverrideComment
        );
        if (validationResponse.isPresent()) {
            String validationComment = validationResponse.get();
            overriddenValues.put(Constants.STATUS, OverrideStatus.FAILURE.toString());
            overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), validationComment);
        } else {
            overriddenValues = getOverriddenFields(bizfinProposedQuantity, quantityOverrideComment);
        }
        return overriddenValues;
    }

    private Map<String, Object> getOverriddenFields(Integer bizfinProposedQuantity, String quantityOverrideComment) {
        Map<String, Object> overriddenValues = new HashMap<>();
        overriddenValues.put(Constants.STATUS, OverrideStatus.SUCCESS.toString());

        if (bizfinProposedQuantity != null) {
            Integer quantityToUse = bizfinProposedQuantity;
            overriddenValues.put(OverrideKey.QUANTITY.toString(), quantityToUse);
            JSONObject overrideComment = new JSONObject();
            overrideComment.put(Constants.QUANTITY_OVERRIDE_COMMENT, quantityOverrideComment);
            overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), overrideComment);
            overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
        } else {
            if (!isEmptyString(quantityOverrideComment)) {
                JSONObject overrideComment = new JSONObject();
                overrideComment.put(Constants.QUANTITY_OVERRIDE_COMMENT, quantityOverrideComment);
                overriddenValues.put(OverrideKey.OVERRIDE_COMMENT.toString(), overrideComment);
                overriddenValues.put(Constants.STATUS, OverrideStatus.UPDATE.toString());
            }
        }
        return overriddenValues;
    }

}
