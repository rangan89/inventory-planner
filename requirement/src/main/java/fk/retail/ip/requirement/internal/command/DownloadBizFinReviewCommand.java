package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadBizFinReviewCommand extends DownloadCommand {

    private LastAppSupplierRepository lastAppSupplierRepository;

    @Inject
    public DownloadBizFinReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository);
    }

    @Override
    protected String getTemplateName() {
        return "/templates/BizFinReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired) {
        if (isLastAppSupplierRequired) {
            fetchLastAppSupplierDataFromProc();
        }
    }


}
