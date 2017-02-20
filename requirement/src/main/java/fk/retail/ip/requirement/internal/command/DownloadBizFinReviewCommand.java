package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementState;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadBizFinReviewCommand extends DownloadCommand {

    @Inject
    public DownloadBizFinReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository, RequirementRepository requirementRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, requirementRepository);
    }

    @Override
    protected String getTemplateName(boolean isLastAppSupplierRequired) {
        if(isLastAppSupplierRequired)
            return "/templates/BizFinReviewWithLastAppSupplier.xlsx";
        else
            return "/templates/BizFinReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired) {
        if (isLastAppSupplierRequired) {
            fetchLastAppSupplierDataFromProc();
        }
        populateBizFinData();
        populateCdoData();
        populateIpcQuantity();
    }


}
