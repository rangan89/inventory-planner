package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Sets;
import fk.retail.ip.requirement.internal.entities.*;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.zulu.client.ZuluClient;
import fk.retail.ip.zulu.internal.entities.RetailProductAttributeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by nidhigupta.m on 20/03/17.
 */

@Slf4j
public class RequirementDataAggregator {

    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    private final ProductInfoRepository productInfoRepository;
    private final LastAppSupplierRepository lastAppSupplierRepository;
    private final RequirementRepository requirementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZuluClient zuluClient;

    public RequirementDataAggregator(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, LastAppSupplierRepository lastAppSupplierRepository,
                                     ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {

        this.fsnBandRepository = fsnBandRepository;
        this.weeklySaleRepository = weeklySaleRepository;
        this.productInfoRepository = productInfoRepository;
        this.zuluClient = zuluClient;
        this.lastAppSupplierRepository = lastAppSupplierRepository;
        this.requirementRepository = requirementRepository;
        this.warehouseRepository = warehouseRepository;

    }

    /*
 Fetch product data for list of fsns from db. If not found from db fetch the data from zulu.
 *
 * */
    protected void fetchProductData(Map<String, List<RequirementDownloadLineItem>> fsnToRequirement) {
        log.info("Fetching Product Data for downloading requirements");
        Set<String> zuluFsns = fetchDataFromProductInfo(fsnToRequirement);
        if (zuluFsns.size() > 0) {
            log.info("Fetching product data from Zulu for download for fns size " + zuluFsns.size());
            fetchDataFromZulu(zuluFsns, fsnToRequirement);
        }
    }



    protected void fetchFsnBandData(Map<String, List<RequirementDownloadLineItem>> fsnToRequirement) {
        log.info("Fetching Fsn Band data for downloading requirements");
        Set<String> fsns = fsnToRequirement.keySet();
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(fsns);
        bands.stream().forEach(b -> {
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(b.getFsn());
            items.forEach(i -> {
                i.setPvBand(b.getPvBand());
                i.setSalesBand(b.getSalesBand());
            });
        });
    }

    protected void fetchSalesBucketData(Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching sales Bucket Data for downloading requirements");
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(fsns);
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);
        requirementDownloadLineItems.forEach(reqItem
                -> populateSalesData(fsnWhWeekSalesMap, currentWeek, reqItem, reqItem::setWeek0Sale, reqItem::setWeek1Sale, reqItem::setWeek2Sale, reqItem::setWeek3Sale, reqItem::setWeek4Sale, reqItem::setWeek5Sale, reqItem::setWeek6Sale, reqItem::setWeek7Sale)
        );
    }

    private void populateSalesData(MultiKeyMap<String,Integer> fsnWhWeekSalesMap, int currentWeek, RequirementDownloadLineItem reqItem, Consumer<Integer>... setters) {
        for (Consumer<Integer> setter : setters) {
            Integer saleQty = fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek));
            setter.accept(saleQty == null ? 0 : saleQty);
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }

    protected void fetchWarehouseName(Set<String> requirementWhs, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching Warehouse name for warehouse code for downloading requirements");
        List<Warehouse> warehouses = warehouseRepository.fetchWarehouseNameByCode(requirementWhs);
        Map<String,String> whCodeNameMap = warehouses.stream().collect(Collectors.toMap(Warehouse::getCode, Warehouse::getName));
        requirementDownloadLineItems.forEach(reqItem -> reqItem.setWarehouseName(Optional.ofNullable(whCodeNameMap.get(reqItem.getWarehouse())).orElse(reqItem.getWarehouse())));
    }

    protected void fetchLastAppSupplierDataFromProc(Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching last app and supplier for downloading requirements");
        List<LastAppSupplier> lastAppSuppliers = lastAppSupplierRepository.fetchLastAppSupplierForFsns(fsns);
        MultiKeyMap<String,Integer> fsnWhLastAppMap = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhLastSupplierMap = new MultiKeyMap();
        lastAppSuppliers.forEach(l -> {
            fsnWhLastAppMap.put(l.getFsn(),l.getWarehouse(),l.getLastApp());
            fsnWhLastSupplierMap.put(l.getFsn(),l.getWarehouse(),l.getLastSupplier());
        });

        requirementDownloadLineItems.forEach(reqItem
                        -> {
                    reqItem.setLastApp(fsnWhLastAppMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                    reqItem.setLastSupplier(fsnWhLastSupplierMap.get(reqItem.getFsn(),reqItem.getWarehouse()));
                }
        );
    }


    protected void populateBizFinData(Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching Biz Fin data for downloading requirements");
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalState.BIZFIN_REVIEW.toString(),fsns);
        MultiKeyMap<String,Integer> fsnWhBizFinRecommended = new MultiKeyMap();
        MultiKeyMap<String,String> fsnWhBizFinComment = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhBizFinRecommended.put(r.getFsn(),r.getWarehouse(), (int) r.getQuantity());
            fsnWhBizFinComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            if (fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse())!=null && !fsnWhBizFinRecommended.get(reqItem.getFsn(),reqItem.getWarehouse()).equals(-1))
                reqItem.setBizFinRecommendedQuantity(fsnWhBizFinRecommended.get(reqItem.getFsn(), reqItem.getWarehouse()));
            reqItem.setBizFinComment(fsnWhBizFinComment.get(reqItem.getFsn(), reqItem.getWarehouse()));
        });
    }


    protected void populateIpcQuantity(Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching IPC Quantity for downloading requirements");
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalState.PROPOSED.toString(),fsns);
        MultiKeyMap<String,Integer> fsnWhIpcProposedQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhIpcProposedQuantity.put(r.getFsn(),r.getWarehouse(), (int) r.getQuantity());
        });
        requirementDownloadLineItems.stream()
                .forEach(reqItem ->reqItem.setIpcProposedQuantity(fsnWhIpcProposedQuantity.get(reqItem.getFsn(),reqItem.getWarehouse())) );
    }

    private void fetchDataFromZulu(Set<String> zuluFsns, Map<String, List<RequirementDownloadLineItem>> fsnToRequirement ) {
        log.info("Fetching zulu data for downloading requirements");
        RetailProductAttributeResponse retailProductAttributeResponse = zuluClient.getRetailProductAttributes(zuluFsns);
        retailProductAttributeResponse.getEntityViews().forEach(entityView -> {
            try {
                String fsn = entityView.getEntityId();
                List<RequirementDownloadLineItem> items = fsnToRequirement.get(fsn);
                Map<String, String> analyticalInfo = (Map<String, String>) entityView.getView().get("analytics_info");
                JSONObject supplyChainJson = new JSONObject(entityView.getView().get("supply_chain").toString());
                String vertical = analyticalInfo.get("vertical");
                String category = analyticalInfo.get("category");
                String superCategory = analyticalInfo.get("super_category");
                JSONObject productAttributesJson = new JSONObject(supplyChainJson.get("product_attributes").toString());
                String brand = productAttributesJson.get("brand").toString();
                int fsp;
                if (productAttributesJson.has("flipkart_selling_price")) {
                    fsp = Integer.parseInt(productAttributesJson.get("flipkart_selling_price").toString());
                } else {
                    fsp = -1;
                }

                String title = supplyChainJson.get("procurement_title").toString();
                items.forEach(i -> {
                    i.setVertical(vertical);
                    i.setCategory(category);
                    i.setSuperCategory(superCategory);
                    i.setBrand(brand);
                    i.setFsp(fsp);
                    i.setTitle(title);
                });
                ProductInfo productInfo = new ProductInfo();
                productInfo.setFsn(fsn);
                productInfo.setBrand(brand);
                productInfo.setCategory(category);
                productInfo.setFsp(fsp);
                productInfo.setSuperCategory(superCategory);
                productInfo.setTitle(title);
                productInfo.setVertical(vertical);
                productInfoRepository.persist(productInfo);
            } catch (Exception e) {
                log.error("Error in fetching data from zulu " + e);
            }} );
    }

    private Set<String> fetchDataFromProductInfo(Map<String, List<RequirementDownloadLineItem>> fsnToRequirement ) {
        log.info("Fetching product info data from db for downloading requirements");
        Set<String> fsns = fsnToRequirement.keySet();
        List<ProductInfo> productInfo = productInfoRepository.getProductInfo(fsns);
        Set<String> cachedFsns = Sets.newHashSet();
        productInfo.stream().forEach(pi -> {
            cachedFsns.add(pi.getFsn());
            List<RequirementDownloadLineItem> items = fsnToRequirement.get(pi.getFsn());
            items.forEach(i -> {
                i.setVertical(pi.getVertical());
                i.setCategory(pi.getCategory());
                i.setSuperCategory(pi.getSuperCategory());
                i.setTitle(pi.getTitle());
                i.setFsp(pi.getFsp());
                i.setBrand(pi.getBrand());
            });
        });
        Set<String> fsnsCopy = Sets.newHashSet(fsns);
        fsnsCopy.removeAll(cachedFsns);
        return fsnsCopy;
    }


    protected void populateCdoData(Set<String> fsns, List<RequirementDownloadLineItem> requirementDownloadLineItems) {
        log.info("Fetching CDO data for downloading requirements");
        List<Requirement> requirements = requirementRepository.findEnabledRequirementsByStateFsn(RequirementApprovalState.CDO_REVIEW.toString(),fsns);
        MultiKeyMap<String,String> fsnWhCdoComment = new MultiKeyMap();
        MultiKeyMap<String,Integer> fsnWhQuantity = new MultiKeyMap();
        requirements.forEach(r -> {
            fsnWhCdoComment.put(r.getFsn(),r.getWarehouse(),r.getOverrideComment());
            fsnWhQuantity.put(r.getFsn(),r.getWarehouse(), (int) r.getQuantity());
        });

        requirementDownloadLineItems.forEach(reqItem
                -> {
            reqItem.setCdoOverrideReason(fsnWhCdoComment.get(reqItem.getFsn(),reqItem.getWarehouse()));
            reqItem.setQuantity(fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
        });
    }

}