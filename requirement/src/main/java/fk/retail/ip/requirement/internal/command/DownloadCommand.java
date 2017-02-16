package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.LastAppSupplier;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.LastAppSupplierRepository;
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


    public DownloadCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository) {
        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.generateExcelCommand = generateExcelCommand;
        this.lastAppSupplierRepository = lastAppSupplierRepository;
    }

    public StreamingOutput execute(List<Requirement> requirements, boolean isLastAppSupplierRequired) {

        requirementDownloadLineItems = requirements.stream().map(RequirementDownloadLineItem::new).collect(toList());
        fsnToRequirement = requirementDownloadLineItems.stream().collect(Collectors.groupingBy(RequirementDownloadLineItem::getFsn));
        requirementFsns = Collections.unmodifiableSet(fsnToRequirement.keySet());
        fetchProductData();
        fetchFsnBandData();
        fetchSalesBucketData();
        fetchRequirementStateData(isLastAppSupplierRequired);

        return generateExcelCommand.generateExcel(requirementDownloadLineItems, getTemplateName());
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

        requirementDownloadLineItems.forEach(reqItem
                -> populateSalesData(sales, reqItem, reqItem::setWeek0Sale, reqItem::setWeek1Sale, reqItem::setWeek2Sale, reqItem::setWeek3Sale, reqItem::setWeek4Sale, reqItem::setWeek5Sale, reqItem::setWeek6Sale, reqItem::setWeek7Sale)
        );
    }

    private void populateSalesData(List<WeeklySale> sales, RequirementDownloadLineItem reqItem, Consumer<Integer>... setters) {
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);

        for (Consumer<Integer> setter : setters) {
            setter.accept(fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek)));
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }

    protected void fetchLastAppSupplierDataFromProc() {
        List<LastAppSupplier> l = lastAppSupplierRepository.fetchLastAppSupplierForFsns(requirementFsns);

        requirementDownloadLineItems.forEach(reqItem
                -> populateLastAppSupplierData(l, reqItem, reqItem::setLastApp, reqItem::setLastSupplier)
        );


    }

    private void populateLastAppSupplierData(List<LastAppSupplier> lastAppSuppliers, RequirementDownloadLineItem reqItem, Consumer<Integer> lastAppSetter, Consumer<String> lastSupplierSetter) {
        MultiKeyMap<String,Integer> fsnWhLastAppMap = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhLastSupplierMap = new MultiKeyMap();
        lastAppSuppliers.forEach(l -> {
            fsnWhLastAppMap.put(l.getFsn(),l.getWarehouseId(),l.getLastApp());
            fsnWhLastSupplierMap.put(l.getFsn(),l.getWarehouseId(),l.getLastSupplier());
        });

        lastAppSetter.accept(fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
        lastSupplierSetter.accept(fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
    }


    protected abstract String getTemplateName();

    abstract void fetchRequirementStateData(boolean isLastAppSupplierRequired);


}
