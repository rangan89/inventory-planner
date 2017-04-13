package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Sets;
import fk.retail.ip.requirement.internal.entities.FsnBand;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.WeeklySale;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementSearchLineItem;
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
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class RequirementSearchDataAggregator {

    private final FsnBandRepository fsnBandRepository;
    private final WeeklySaleRepository weeklySaleRepository;
    private final ProductInfoRepository productInfoRepository;
    private final LastAppSupplierRepository lastAppSupplierRepository;
    private final RequirementRepository requirementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ZuluClient zuluClient;

    public RequirementSearchDataAggregator(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, LastAppSupplierRepository lastAppSupplierRepository,
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
    protected void fetchProductData(Map<String, List<RequirementSearchLineItem>> fsnToRequirement) {
        log.info("Fetching Product Data for search requirements");
        Set<String> zuluFsns = fetchDataFromProductInfo(fsnToRequirement);
        if (zuluFsns.size() > 0) {
            log.info("Fetching product data from Zulu for search for fns size " + zuluFsns.size());
            fetchDataFromZulu(zuluFsns, fsnToRequirement);
        }
    }



    protected void fetchFsnBandData(Map<String, List<RequirementSearchLineItem>> fsnToRequirement) {
        log.info("Fetching Fsn Band data for search requirements");
        Set<String> fsns = fsnToRequirement.keySet();
        List<FsnBand> bands = fsnBandRepository.fetchBandDataForFSNs(fsns);
        bands.stream().forEach(b -> {
            List<RequirementSearchLineItem> items = fsnToRequirement.get(b.getFsn());
            items.forEach(i -> {
                i.setPvBand(b.getPvBand());
                i.setSalesBand(b.getSalesBand());
            });
        });
    }

    protected void fetchSalesBucketData(Set<String> fsns, List<RequirementSearchLineItem> requirementSearchLineItems) {
        log.info("Fetching sales Bucket Data for search requirements");
        List<WeeklySale> sales = weeklySaleRepository.fetchWeeklySalesForFsns(fsns);
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleQty()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 2).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear);
        requirementSearchLineItems.forEach(reqItem
                -> populateSalesData(fsnWhWeekSalesMap, currentWeek, reqItem, reqItem::setWeek0Sale, reqItem::setWeek1Sale, reqItem::setWeek2Sale, reqItem::setWeek3Sale, reqItem::setWeek4Sale, reqItem::setWeek5Sale, reqItem::setWeek6Sale, reqItem::setWeek7Sale)
        );
    }

    private void populateSalesData(MultiKeyMap<String,Integer> fsnWhWeekSalesMap, int currentWeek, RequirementSearchLineItem reqItem, Consumer<Integer>... setters) {
        for (Consumer<Integer> setter : setters) {
            Integer saleQty = fsnWhWeekSalesMap.get(reqItem.getFsn(), reqItem.getWarehouse(), String.valueOf(currentWeek));
            setter.accept(saleQty == null ? 0 : saleQty);
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }


    private void fetchDataFromZulu(Set<String> zuluFsns, Map<String, List<RequirementSearchLineItem>> fsnToRequirement ) {
        log.info("Fetching zulu data for search requirements");
        RetailProductAttributeResponse retailProductAttributeResponse = zuluClient.getRetailProductAttributes(zuluFsns);
        retailProductAttributeResponse.getEntityViews().forEach(entityView -> {
            try {
                String fsn = entityView.getEntityId();
                List<RequirementSearchLineItem> items = fsnToRequirement.get(fsn);
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

    private Set<String> fetchDataFromProductInfo(Map<String, List<RequirementSearchLineItem>> fsnToRequirement ) {
        log.info("Fetching product info data from db for search requirements");
        Set<String> fsns = fsnToRequirement.keySet();
        List<ProductInfo> productInfo = productInfoRepository.getProductInfo(fsns);
        Set<String> cachedFsns = Sets.newHashSet();
        productInfo.stream().forEach(pi -> {
            cachedFsns.add(pi.getFsn());
            List<RequirementSearchLineItem> items = fsnToRequirement.get(pi.getFsn());
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

}
