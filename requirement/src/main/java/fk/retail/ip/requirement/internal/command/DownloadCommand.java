package fk.retail.ip.requirement.internal.command;

import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 26/01/17.
 */

@Slf4j
public abstract class DownloadCommand extends RequirementDataAggregator {

    private final GenerateExcelCommand generateExcelCommand;

    public DownloadCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                           ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {
        super(fsnBandRepository, weeklySaleRepository,lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository, warehouseRepository);
        this.generateExcelCommand = generateExcelCommand;
    }

    public StreamingOutput execute(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        log.info("Download Request for {} number of requirements", requirements.size());
        if (requirements.isEmpty()) {
            log.info("No requirements found for download. Generating empty file");
            return generateExcelCommand.generateExcel(Collections.EMPTY_LIST, getTemplateName(isLastAppSupplierRequired));
        }
        List<RequirementDownloadLineItem> requirementDownloadLineItems = requirements.stream().map(RequirementDownloadLineItem::new).collect(toList());
        Map<String, List<RequirementDownloadLineItem>> fsnToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getFsn));
        Map<String, List<RequirementDownloadLineItem>> WhToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getWarehouse));
        Set<String> fsns = fsnToRequirement.keySet();
        Set<String> requirementWhs =  WhToRequirement.keySet();
        fetchProductData(fsnToRequirement);
        fetchFsnBandData(fsnToRequirement);
        fetchSalesBucketData(fsns, requirementDownloadLineItems);
        fetchWarehouseName(requirementWhs,requirementDownloadLineItems);
        fetchRequirementStateData(isLastAppSupplierRequired, fsns,requirementDownloadLineItems);
        return generateExcelCommand.generateExcel(requirementDownloadLineItems, getTemplateName(isLastAppSupplierRequired));
    }

    protected abstract String getTemplateName(boolean isLastAppSupplierRequired);

    abstract void fetchRequirementStateData(boolean isLastAppSupplierRequired, Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems);

}
