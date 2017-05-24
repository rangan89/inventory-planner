package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.context.ForecastContext;
import fk.retail.ip.requirement.internal.context.OnHandQuantityContext;
import fk.retail.ip.requirement.internal.context.PolicyContext;
import fk.retail.ip.requirement.internal.entities.Forecast;
import fk.retail.ip.requirement.internal.entities.Group;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.IwtRequestItem;
import fk.retail.ip.requirement.internal.entities.OpenRequirementAndPurchaseOrder;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Projection;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.Warehouse;
import fk.retail.ip.requirement.internal.entities.WarehouseInventory;
import fk.retail.ip.requirement.internal.enums.EventType;
import fk.retail.ip.requirement.internal.enums.FdpRequirementEventType;
import fk.retail.ip.requirement.internal.enums.OverrideKey;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.ForecastRepository;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.internal.repository.ProjectionRepository;
import fk.retail.ip.requirement.internal.repository.RequirementEventLogRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseInventoryRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseRepository;
import fk.retail.ip.requirement.model.RequirementChangeMap;
import fk.retail.ip.requirement.model.RequirementChangeRequest;
import fk.retail.ip.ssl.client.SslClient;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import fk.retail.ip.ssl.model.SupplierView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalculateRequirementCommand {

    private final WarehouseRepository warehouseRepository;
    private final GroupFsnRepository groupFsnRepository;
    private final PolicyRepository policyRepository;
    private final ForecastRepository forecastRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final IwtRequestItemRepository iwtRequestItemRepository;
    private final OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository;
    private final RequirementRepository requirementRepository;
    private final ProductInfoRepository productInfoRepository;
    private final SslClient sslClient;
    //TODO: remove
    private final ProjectionRepository projectionRepository;
    private final ObjectMapper objectMapper;
    private final FdpRequirementIngestorImpl fdpRequirementIngestor;
    private final RequirementHelper requirementHelper;
    private final RequirementEventLogRepository requirementEventLogRepository;

    private Set<String> fsns = Sets.newHashSet();
    private Map<String, String> warehouseCodeMap = Maps.newHashMap();
    private Map<String, Group> fsnToGroupMap;
    private PolicyContext policyContext;
    private ForecastContext forecastContext;
    private OnHandQuantityContext onHandQuantityContext;

    @Inject
    public CalculateRequirementCommand(
            WarehouseRepository warehouseRepository,
            GroupFsnRepository groupFsnRepository,
            PolicyRepository policyRepository,
            ForecastRepository forecastRepository,
            WarehouseInventoryRepository warehouseInventoryRepository,
            IwtRequestItemRepository iwtRequestItemRepository,
            OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository,
            RequirementRepository requirementRepository,
            ProductInfoRepository productInfoRepository,
            RequirementHelper requirementHelper,
            SslClient sslClient,
            ProjectionRepository projectionRepository,
            ObjectMapper objectMapper,
            FdpRequirementIngestorImpl fdpRequirementIngestor,
            RequirementEventLogRepository requirementEventLogRepository
    ) {
        this.warehouseRepository = warehouseRepository;
        this.groupFsnRepository = groupFsnRepository;
        this.policyRepository = policyRepository;
        this.forecastRepository = forecastRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.iwtRequestItemRepository = iwtRequestItemRepository;
        this.openRequirementAndPurchaseOrderRepository = openRequirementAndPurchaseOrderRepository;
        this.requirementRepository = requirementRepository;
        this.productInfoRepository = productInfoRepository;
        this.requirementHelper = requirementHelper;
        this.sslClient = sslClient;
        this.projectionRepository = projectionRepository;
        this.objectMapper = objectMapper;
        this.fdpRequirementIngestor = fdpRequirementIngestor;
        this.requirementEventLogRepository = requirementEventLogRepository;
    }

    public CalculateRequirementCommand withFsns(Set<String> fsns) {
        this.fsns = fsns;
        return this;
    }

    private Set<String> initContexts() {
        this.warehouseCodeMap = getWarehouseCodeMap();
        this.fsnToGroupMap = getFsnToGroupMap(fsns);
        Set<String> validFsns = fsnToGroupMap.keySet();
        this.forecastContext = getForecastContext(validFsns);
        validFsns = forecastContext.getFsns();
        this.policyContext = getPolicyContext(validFsns);
        this.onHandQuantityContext = getOnHandQuantityContext(validFsns);
        return validFsns;
    }

    public void execute() {
        List<RequirementChangeRequest> requirementChangeRequestList = Lists.newArrayList();

        //mark existing requirements ad disabled
        List<Requirement> existingRequirements = requirementRepository.find(fsns, true);
        existingRequirements.forEach(requirement -> {
            requirement.setEnabled(false);
            requirement.setCurrent(false);
        });
        //TODO: remove
        List<Projection> existingProjections = projectionRepository.find(fsns, true);
        existingProjections.forEach(projection -> projection.setEnabled(0));

        Set<String> validFsns = initContexts();


        //create requirement entities
        List<Requirement> allRequirements = Lists.newArrayList();
        Map<String, List<Requirement>> fsnToRequirementMap = Maps.newHashMap();
        for (String fsn : validFsns) {
            Set<String> warehouses = forecastContext.getWarehouses(fsn);
            for (String warehouse : warehouses) {
                Requirement requirement = getRequirement(fsn, warehouse, fsnToGroupMap.get(fsn));
                if (fsnToRequirementMap.containsKey(fsn) && fsnToRequirementMap.get(fsn) != null) {
                    fsnToRequirementMap.get(fsn).add(requirement);
                } else {
                    fsnToRequirementMap.put(fsn, Lists.newArrayList(requirement));
                }
                allRequirements.add(requirement);
            }
        }

        //apply policies, mark error if critical policy is missing
        validFsns.forEach(fsn -> {
            List<Requirement> requirements = fsnToRequirementMap.get(fsn);
            policyContext.applyPolicies(fsn, requirements, forecastContext, onHandQuantityContext, requirementChangeRequestList);
            //the quantity has to be rounded after policy application
            requirements.forEach(requirement -> requirement.setQuantity(Math.round(requirement.getQuantity())));
        });

        //find supplier for non error fsns
        List<Requirement> validRequirements = allRequirements.stream().filter(requirement -> !RequirementApprovalState.ERROR.toString().equals(requirement.getState())).collect(Collectors.toList());
        populateSupplier(validRequirements,requirementChangeRequestList);


        //create dummy error entry for fsns without forecast or group
        Set<String> fsnsWithoutGroups = new HashSet<>(fsns);
        fsnsWithoutGroups.removeAll(fsnToGroupMap.keySet());
        Set<Requirement> erredRequirements = fsnsWithoutGroups.stream().map(fsn -> getErredRequirement(fsn, Constants.GROUP_NOT_FOUND)).collect(Collectors.toSet());

        Set<String> fsnsWithoutForecast = new HashSet<>(fsnToGroupMap.keySet());
        fsnsWithoutForecast.removeAll(forecastContext.getFsns());
        erredRequirements.addAll(fsnsWithoutForecast.stream().map(fsn -> getErredRequirement(fsn, Constants.FORECAST_NOT_FOUND)).collect(Collectors.toSet()));

        allRequirements.addAll(erredRequirements);

        //TODO: remove backward compatibility changes to add entry in projections table
        for (String fsn : fsnToRequirementMap.keySet()) {
            List<Requirement> requirements = fsnToRequirementMap.get(fsn);
            String state = RequirementApprovalState.ERROR.toString();
            for (Requirement requirement : requirements) {

                if (RequirementApprovalState.PRE_PROPOSED == RequirementApprovalState.fromString(requirement.getState())) {
                    state = RequirementApprovalState.PRE_PROPOSED.toString();
                    break;
                }
            }
            Projection projection = new Projection();
            Requirement requirement = requirements.get(0);
            projection.setFsn(requirement.getFsn());
            projection.setCurrentState(state);
            projection.setEnabled(RequirementApprovalState.ERROR.toString().equals(state) ? 0 : 1);
            projection.setError("YOLO");
            projection.setProcType(requirement.getProcType());
            projection.setForecastId(0L);
            projection.setIntransit(0);
            projection.setInventory(0);
            projection.setPolicyId("SWAG");
            projection.setGroupId(requirement.getRequirementSnapshot().getGroup().getId());
            projectionRepository.persist(projection);
            requirements.forEach(requirement1 -> {
                requirement1.setProjectionId(projection.getId());
            });
        }
        //save
        requirementRepository.persist(allRequirements);

        //Add PROJECTION_CREATED events to fdp request
        log.debug("Adding PROJECTION_CREATED events to fdp request");
        addProjectionCreatedRequest(allRequirements, requirementChangeRequestList);

        EventLogger eventLogger = new EventLogger(requirementEventLogRepository);
        eventLogger.insertEvent(requirementChangeRequestList, EventType.REQUIREMENT_CREATION);

        //Push PROJECTION_CREATED, SUPPLIER_ASSIGNED and APP_ASSIGNED events to fdp
        log.debug("Pushing PROJECTION_CREATED, SUPPLIER_ASSIGNED and APP_ASSIGNED events to fdp");
        fdpRequirementIngestor.pushToFdp(requirementChangeRequestList);
    }

    private void addProjectionCreatedRequest(List<Requirement> allRequirements, List<RequirementChangeRequest> requirementChangeRequestList) {

        allRequirements.forEach(requirement -> {
            if(!requirement.getState().equals(RequirementApprovalState.ERROR.toString())) {
                RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.STATE.toString(), null, RequirementApprovalState.PRE_PROPOSED.toString(), FdpRequirementEventType.PROJECTION_CREATED.toString(), "Projection created", "system"));
                requirementChangeRequest.setRequirement(requirement);
                requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                requirementChangeRequestList.add(requirementChangeRequest);
            }
        });
    }

    private Requirement getErredRequirement(String fsn, String errorMessage) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setState(RequirementApprovalState.ERROR.toString());
        requirement.setWarehouse(Constants.NOT_APPLICABLE);
        requirement.setOverrideComment(errorMessage);
        requirement.setEnabled(true);
        requirement.setCurrent(true);
        return requirement;
    }

    private void populateSupplier(List<Requirement> requirements, List<RequirementChangeRequest> requirementChangeRequestList) {
        List<SupplierSelectionRequest> requests = requirementHelper.createSupplierSelectionRequest(requirements);
        List<SupplierSelectionResponse> responses = sslClient.getSupplierSelectionResponse(requests);
        if (requests.size() != responses.size()) {
            requirements.forEach(requirement -> {
                requirement.setState(RequirementApprovalState.ERROR.toString());
                requirement.setOverrideComment(Constants.SSL_API_FAILED);
                requirement.setEnabled(true);
                requirement.setCurrent(true);
            });
            return;
        }
        Table<String, String, SupplierSelectionResponse> fsnWhSupplierTable = HashBasedTable.create();
        responses.stream().filter(response -> response.getSuppliers().size() > 0).forEach(response -> {
            fsnWhSupplierTable.put(response.getFsn(), response.getWarehouseId(), response);
        });
        List<ProductInfo> productInfos = productInfoRepository.getProductInfo(forecastContext.getFsns());
        Map<String, String> fsnToVerticalMap = productInfos.stream().collect(Collectors.toMap(ProductInfo::getFsn, ProductInfo::getVertical, (k1, k2) -> k1));
        requirements.forEach(requirement -> {
            SupplierSelectionResponse supplierResponse = fsnWhSupplierTable.get(requirement.getFsn(), requirement.getWarehouse());
            if (supplierResponse != null) {
                RequirementChangeRequest requirementChangeRequest = new RequirementChangeRequest();
                List<RequirementChangeMap> requirementChangeMaps = Lists.newArrayList();
                SupplierView supplier = supplierResponse.getSuppliers().get(0);
                requirement.setSupplier(supplier.getSourceId());
                requirement.setApp(supplier.getApp());
                requirement.setMrp(supplier.getMrp());
                int sla = requirementHelper.getSla(fsnToVerticalMap.get(requirement.getFsn()), requirement.getWarehouse(), supplier.getSourceId(), supplier.getSla());
                requirement.setSla(sla);
                requirement.setCurrency(supplier.getVendorPreferredCurrency());
                requirement.setMrpCurrency(supplier.getVendorPreferredCurrency());
                requirement.setInternational(!supplier.isLocal());
                requirement.setSslId(supplierResponse.getEntityId());
                //Add SUPPLIER_ASSIGNED and APP_ASSIGNED events to fdp request
                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.SUPPLIER.toString(), null, supplier.getSourceId(), FdpRequirementEventType.SUPPLIER_ASSIGNED.toString(), "Supplier assigned", "system"));
                requirementChangeMaps.add(PayloadCreationHelper.createChangeMap(OverrideKey.APP.toString(), null, String.valueOf(supplier.getApp()), FdpRequirementEventType.APP_ASSIGNED.toString(), "App assigned", "system"));
                requirementChangeRequest.setRequirement(requirement);
                requirementChangeRequest.setRequirementChangeMaps(requirementChangeMaps);
                requirementChangeRequestList.add(requirementChangeRequest);
            }
        });
    }

    public Map<String, String> getWarehouseCodeMap() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return warehouses.stream().collect(Collectors.toMap(Warehouse::getName, Warehouse::getCode));
    }

    private Map<String, Group> getFsnToGroupMap(Set<String> fsns) {
        List<GroupFsn> groupFsns = groupFsnRepository.findByFsns(fsns);
        return groupFsns.stream().collect(Collectors.toMap(GroupFsn::getFsn, GroupFsn::getGroup));
    }

    private Requirement getRequirement(String fsn, String warehouse, Group group) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setWarehouse(warehouse);
        requirement.setState(RequirementApprovalState.PRE_PROPOSED.toString());
        requirement.setEnabled(true);
        requirement.setCurrent(true);
//        requirement.setQuantity(0);
        //TODO: do we need procType here?
        requirement.setProcType(Constants.FORWARD_PLANNING_PROCUREMENT_TYPE);
        RequirementSnapshot requirementSnapshot = new RequirementSnapshot();
        requirementSnapshot.setGroup(group);
        requirementSnapshot.setForecast(forecastContext.getForecastAsString(fsn, warehouse));
        requirementSnapshot.setOpenReqQty((int) onHandQuantityContext.getOpenRequirementQuantity(fsn, warehouse));
        requirementSnapshot.setPendingPoQty((int) onHandQuantityContext.getPendingPurchaseOrderQuantity(fsn, warehouse));
        requirementSnapshot.setIwitIntransitQty((int) onHandQuantityContext.getIwtQuantity(fsn, warehouse));
        requirementSnapshot.setInventoryQty((int) onHandQuantityContext.getInventoryQuantity(fsn, warehouse));
        requirementSnapshot.setQoh((int) onHandQuantityContext.getOnHandInventoryQuantity(fsn, warehouse));
        requirement.setRequirementSnapshot(requirementSnapshot);
        return requirement;
    }

    private ForecastContext getForecastContext(Set<String> fsns) {
        List<Forecast> forecasts = forecastRepository.fetchByFsns(fsns);
        ForecastContext forecastContext = new ForecastContext(objectMapper);
        forecasts.forEach(forecast -> forecastContext.addForecast(forecast.getFsn(), forecast.getWarehouse(), forecast.getForecast()));
        return forecastContext;
    }

    private PolicyContext getPolicyContext(Set<String> fsns) {
        PolicyContext policyContext = new PolicyContext(objectMapper, warehouseCodeMap);
        //add group level policies to context
        Set<Long> groupIds = fsns.stream().map(fsn -> fsnToGroupMap.get(fsn).getId()).collect(Collectors.toSet());
        List<Policy> groupPolicies = policyRepository.fetchByGroup(groupIds);
        Map<Long, List<Policy>> groupIdToPoliciesMap = Maps.newHashMap();
        groupPolicies.forEach(policy -> {
            List<Policy> policies = groupIdToPoliciesMap.get(policy.getGroup().getId());
            if (policies == null) {
                policies = Lists.newArrayList();
            }
            policies.add(policy);
            groupIdToPoliciesMap.put(policy.getGroup().getId(), policies);
        });
        fsns.forEach(fsn -> {
            Long groupId = fsnToGroupMap.get(fsn).getId();
            List<Policy> policies = groupIdToPoliciesMap.get(groupId);
            if (policies != null) {
                policies.forEach(policy -> policyContext.addPolicy(fsn, policy.getPolicyType(), policy.getValue()));
            }
        });
        //override with fsn level policies
        List<Policy> policies = policyRepository.fetch(fsns);
        policies.forEach(policy -> policyContext.addPolicy(policy.getFsn(), policy.getPolicyType(), policy.getValue()));

        return policyContext;
    }

    private OnHandQuantityContext getOnHandQuantityContext(Set<String> fsns) {
        OnHandQuantityContext onHandQuantityContext = new OnHandQuantityContext();
        //add wh inventory
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.fetchByFsns(fsns);
        warehouseInventories.forEach(warehouseInventory -> onHandQuantityContext.addInventoryQuantity(warehouseInventory.getFsn(), warehouseInventory.getWarehouse(), warehouseInventory.getQuantity(), warehouseInventory.getQoh()));
        //add open req and pending po
        List<OpenRequirementAndPurchaseOrder> openReqAndPOs = openRequirementAndPurchaseOrderRepository.fetchByFsns(fsns);
        openReqAndPOs.forEach(openReqAndPO -> {
            onHandQuantityContext.addOpenRequirementAndPurchaseOrder(
                    openReqAndPO.getFsn(), openReqAndPO.getWarehouse(),
                    openReqAndPO.getOpenRequirementQuantity(),
                    openReqAndPO.getPendingPurchaseOrderQuantity());
        });
        //add iwt intransit
        List<IwtRequestItem> iwtRequestItems = iwtRequestItemRepository.fetchByFsns(fsns, Constants.INTRANSIT_REQUEST_STATUSES);
        iwtRequestItems.forEach(iwtRequestItem -> onHandQuantityContext.addIwtQuantity(iwtRequestItem.getFsn(), iwtRequestItem.getWarehouse(), iwtRequestItem.getAvailableQuantity()));
        return onHandQuantityContext;
    }

}
