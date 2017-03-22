package fk.retail.ip.requirement.internal.command.download;

import com.google.inject.Inject;

import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;

import java.util.List;
import java.util.Set;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadBizFinReviewCommand extends DownloadCommand {

    @Inject
    public DownloadBizFinReviewCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                                       ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository, warehouseRepository);

    }

    @Override
    protected String getTemplateName(boolean isLastAppSupplierRequired) {
        if(isLastAppSupplierRequired)
            return "/templates/BizFinReviewWithLastAppSupplier.xlsx";
        else
            return "/templates/BizFinReview.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired, Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        if (isLastAppSupplierRequired) {
            fetchLastAppSupplierDataFromProc(requirementFsns,requirementDownloadLineItems );
        }
        populateCdoData(requirementFsns,requirementDownloadLineItems );
        populateIpcQuantity(requirementFsns,requirementDownloadLineItems);
        populateBizFinData(requirementFsns,requirementDownloadLineItems );
    }

}
