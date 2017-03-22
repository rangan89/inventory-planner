package fk.retail.ip.requirement.internal.command.download;

import com.google.inject.Inject;

import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;

import java.util.List;
import java.util.Set;


/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadCDOReviewCommand extends DownloadCommand {


   @Inject
    public DownloadCDOReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                                    ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository, warehouseRepository);
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
