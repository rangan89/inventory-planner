package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementState;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadIPCReviewCommand extends DownloadCommand {

    @Inject
    public DownloadIPCReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository, RequirementRepository requirementRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, requirementRepository);
    }

    @Override
    protected String getTemplateName(boolean isLastAppSupplierRequired) {
        return "/templates/IPCReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired) {

        populateBizFinData();
        populateCdoData();
        populateIpcQuantity();
    }






}
