package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
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
                                      ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository);

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
        populateBizFinData(requirementFsns,requirementDownloadLineItems );
        populateCdoData(requirementFsns,requirementDownloadLineItems );
        populateIpcQuantity(requirementFsns,requirementDownloadLineItems);
    }

}
