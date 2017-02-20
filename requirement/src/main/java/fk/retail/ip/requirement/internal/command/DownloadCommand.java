package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.enums.RequirementState;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.collections4.map.MultiKeyMap;

import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public abstract class DownloadCommand {

    protected Set<String> requirementFsns;
    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    protected List<RequirementDownloadLineItem> requirementDownloadLineItems;
    private Map<String, List<RequirementDownloadLineItem>> fsnToRequirement;
    private final LastAppSupplierRepository lastAppSupplierRepository;
    private final GenerateExcelCommand generateExcelCommand;
    private final RequirementRepository requirementRepository;


    public DownloadCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository, RequirementRepository requirementRepository) {
        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.generateExcelCommand = generateExcelCommand;
        this.lastAppSupplierRepository = lastAppSupplierRepository;
        this.requirementRepository = requirementRepository;
    }

    public StreamingOutput execute(List<Requirement> requirements, boolean isLastAppSupplierRequired) {

        requirementDownloadLineItems = requirements.stream().map(RequirementDownloadLineItem::new).collect(toList());
        fsnToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getFsn));
        requirementFsns = Collections.unmodifiableSet(fsnToRequirement.keySet());
        fetchProductData();
        fetchFsnBandData();
        fetchSalesBucketData();
        fetchRequirementStateData(isLastAppSupplierRequired);

        return generateExcelCommand.generateExcel(requirementDownloadLineItems, getTemplateName(isLastAppSupplierRequired));
    }

    protected void fetchProductData() {

    }

    protected void fetchFsnBandData() {
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(requirementFsns);
        bands.stream().forEach(b -> {
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(b.getFsn());
            items.forEach(i -> {
                i.setPvBand(b.getPvBand());
                i.setSalesBand(b.getSalesBand());
            });
        });
    }

    protected void fetchSalesBucketData() {
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(requirementFsns);
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);
        requirementDownloadLineItems.forEach(reqItem
                -> populateSalesData(fsnWhWeekSalesMap, currentWeek, reqItem, reqItem::setWeek0Sale, reqItem::setWeek1Sale, reqItem::setWeek2Sale, reqItem::setWeek3Sale, reqItem::setWeek4Sale, reqItem::setWeek5Sale, reqItem::setWeek6Sale, reqItem::setWeek7Sale)
        );
    }

    private void populateSalesData(MultiKeyMap<String,Integer> fsnWhWeekSalesMap, int currentWeek, RequirementDownloadLineItem reqItem, Consumer<Integer>... setters) {
        for (Consumer<Integer> setter : setters) {
            setter.accept(fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek)));
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }

    protected void fetchLastAppSupplierDataFromProc() {
        List<LastAppSupplier> lastAppSuppliers = lastAppSupplierRepository.fetchLastAppSupplierForFsns(requirementFsns);
        MultiKeyMap<String,Integer> fsnWhLastAppMap = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhLastSupplierMap = new MultiKeyMap();
        lastAppSuppliers.forEach(l -> {
            fsnWhLastAppMap.put(l.getFsn(),l.getWarehouseId(),l.getLastApp());
            fsnWhLastSupplierMap.put(l.getFsn(),l.getWarehouseId(),l.getLastSupplier());
        });

        requirementDownloadLineItems.forEach(reqItem
                        -> {
                    if (fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                        reqItem.setLastApp(fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                    if (fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                        reqItem.setLastSupplier(fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                }
        );
    }

    protected void populateBizFinData() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("bizfin_review",requirementFsns);
        MultiKeyMap<String,Integer> fsnWhBizFinRecommended = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhBizFinComment = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhBizFinRecommended.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
            fsnWhBizFinComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setBizFinRecommendedQuantity(fsnWhBizFinRecommended.get(reqItem.getFsn(), reqItem.getWarehouse()));
            if (fsnWhBizFinComment.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setBizFinComment(fsnWhBizFinComment.get(reqItem.getFsn(), reqItem.getWarehouse()));
        });
    }

    protected void populateIpcQuantity() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("proposed",requirementFsns);
        MultiKeyMap<String,Integer> fsnWhIpcProposedQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhIpcProposedQuantity.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhIpcProposedQuantity.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setIpcProposedQuantity(fsnWhIpcProposedQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
        });
    }

    protected void populateCdoData() {
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn("cdo_review",requirementFsns);
        MultiKeyMap<String,String> fsnWhCdoComment = new MultiKeyMap();
        MultiKeyMap<String,Integer> fsnWhQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhCdoComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
            fsnWhQuantity.put(r.getFsn(),r.getWarehouse(),r.getQuantity());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhCdoComment.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setCdoOverrideReason(fsnWhCdoComment.get(reqItem.getFsn(),reqItem.getWarehouse()));
            if (fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse())!=null)
                reqItem.setQuantity(fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
        });
    }

    protected abstract String getTemplateName(boolean isLastAppSupplierRequired);

    abstract void fetchRequirementStateData(boolean isLastAppSupplierRequired);


}
