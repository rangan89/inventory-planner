package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementState;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadCDOReviewCommand extends DownloadCommand {

    @Inject
    public DownloadCDOReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository, RequirementRepository requirementRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, requirementRepository);
    }

    @Override
    protected String getTemplateName(boolean isLastAPPSupplierRequired) {
        if(isLastAPPSupplierRequired)
            return "/templates/CDOReviewWithLastAppSupplier.xlsx";
        else
            return "/templates/CDOReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired) {
        if (isLastAppSupplierRequired) {
            fetchLastAppSupplierDataFromProc();
        }
        populateBizFinData();
    }

}
