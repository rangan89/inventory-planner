package fk.retail.ip.requirement.internal.command.download;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;

import java.util.List;
import java.util.Set;

/**
 * Created by yogeshwari.k on 23/02/17.
 */
public class DownloadPreProposedCommand extends DownloadCommand {

    @Inject
    public DownloadPreProposedCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository, ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand, lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository, warehouseRepository);
    }

    @Override
    protected String getTemplateName(boolean isLastAppSupplierRequired) {
        return "/templates/pre_proposed.xlsx";
    }

    @Override
    void fetchRequirementStateData(boolean isLastAppSupplierRequired, Set<String> requirementFsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {

    }
}
