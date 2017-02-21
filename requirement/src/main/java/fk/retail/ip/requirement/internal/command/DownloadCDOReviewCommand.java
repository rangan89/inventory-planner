package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;

import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadCDOReviewCommand extends DownloadCommand {


   @Inject
    public DownloadCDOReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                                      ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository);
}


    @Override
    protected String getTemplateName(boolean isLastAPPSupplierRequired) {
        if(isLastAPPSupplierRequired)
            return "/templates/CDOReviewWithLastAppSupplier.xlsx";
        else
            return "/templates/CDOReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired, Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        if (isLastAppSupplierRequired) {
            fetchLastAppSupplierDataFromProc(requirementFsns,requirementDownloadLineItems);
        }
        populateBizFinData(requirementFsns,requirementDownloadLineItems);
    }

}
