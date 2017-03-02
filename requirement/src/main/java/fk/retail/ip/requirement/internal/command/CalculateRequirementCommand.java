package fk.retail.ip.requirement.internal.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
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
import fk.retail.ip.requirement.internal.entities.Projection;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.retail.ip.requirement.internal.entities.WarehouseInventory;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalStates;
import fk.retail.ip.requirement.internal.repository.ForecastRepository;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.IwtRequestItemRepository;
import fk.retail.ip.requirement.internal.repository.OpenRequirementAndPurchaseOrderRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.repository.WarehouseInventoryRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CalculateRequirementCommand {

    private Set<String> fsns = Sets.newHashSet();
    private final GroupFsnRepository groupFsnRepository;
    private final PolicyRepository policyRepository;
    private final ForecastRepository forecastRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final IwtRequestItemRepository iwtRequestItemRepository;
    private final OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository;
    private final RequirementRepository requirementRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, Group> fsnToGroupMap;
    private final PolicyContext policyContext;
    private final ForecastContext forecastContext;
    private final OnHandQuantityContext onHandQuantityContext;

    @Inject
    public CalculateRequirementCommand(GroupFsnRepository groupFsnRepository, PolicyRepository policyRepository, ForecastRepository forecastRepository, WarehouseInventoryRepository warehouseInventoryRepository, IwtRequestItemRepository iwtRequestItemRepository, OpenRequirementAndPurchaseOrderRepository openRequirementAndPurchaseOrderRepository, RequirementRepository requirementRepository, ObjectMapper objectMapper) {
        this.groupFsnRepository = groupFsnRepository;
        this.policyRepository = policyRepository;
        this.forecastRepository = forecastRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.iwtRequestItemRepository = iwtRequestItemRepository;
        this.openRequirementAndPurchaseOrderRepository = openRequirementAndPurchaseOrderRepository;
        this.requirementRepository = requirementRepository;
        this.objectMapper = objectMapper;
        this.fsnToGroupMap = getFsnToGroupMap();
        this.forecastContext = getForecastContext();
        this.policyContext = getPolicyContext(forecastContext.getFsns());
        this.onHandQuantityContext = getOnHandQuantityContext(forecastContext.getFsns());
    }

    public CalculateRequirementCommand withFsns(Set<String> fsns) {
        this.fsns = fsns;
        return null;
    }

    public void execute() {
        Set<String> fsnsWithForecast = forecastContext.getFsns();

        //create requirement entities
        Map<String, List<Requirement>> fsnToRequirementMap = Maps.newHashMap();
        for (String fsn : fsnsWithForecast) {
            Set<String> warehouses = forecastContext.getWarehouses(fsn);
            for (String warehouse : warehouses) {
                Requirement requirement = getRequirement(fsn, warehouse, fsnToGroupMap.get(fsn));
                if (fsnToRequirementMap.containsKey(fsn) && fsnToRequirementMap.get(fsn) != null) {
                    fsnToRequirementMap.get(fsn).add(requirement);
                } else {
                    fsnToRequirementMap.put(fsn, Lists.newArrayList(requirement));
                }
            }
        }

        //apply policies, mark error if critical policy is missing
        fsnsWithForecast.forEach(fsn -> {
            List<Requirement> requirements = fsnToRequirementMap.get(fsn);
            policyContext.applyPolicies(fsn, requirements, forecastContext, onHandQuantityContext);
        });

        //find supplier for non error fsns
        List<Requirement> allRequirements = Lists.newArrayList();
        fsnToRequirementMap.values().forEach(requirements -> allRequirements.addAll(allRequirements));
        List<Requirement> validRequirements = allRequirements.stream().filter(requirement -> !Constants.ERROR_STATE.equals(requirement.getState())).collect(Collectors.toList());
        populateSupplier(validRequirements);

        //create dummy error entry for fsns without forecast
        Set<String> fsnsWithoutForecast = new HashSet<>(fsns);
        fsnsWithoutForecast.removeAll(fsnsWithForecast);
        Set<Requirement> erredRequirements = fsnsWithoutForecast.stream().map(fsn -> {
            Requirement requirement = new Requirement();
            requirement.setFsn(fsn);
            requirement.setState(Constants.ERROR_STATE);
            requirement.setWarehouse(Constants.NOT_APPLICABLE);
            requirement.setOverrideComment(Constants.FORECAST_NOT_FOUND);
            requirement.setEnabled(false);
            return requirement;
        }).collect(Collectors.toSet());
        allRequirements.addAll(erredRequirements);

        //TODO: remove backward compatibility changes to add entry in projections table
        for (String fsn : fsnToRequirementMap.keySet()) {
            List<Requirement> requirements = fsnToRequirementMap.get(fsn);
            Projection projection = new Projection();
            Requirement requirement = requirements.get(0);
            projection.setFsn(requirement.getFsn());
            projection.setCurrentState(requirement.getState());
            projection.setEnabled(requirement.getEnabled()?1:0);
            projection.setError(requirement.getOverrideComment());
            projection.setProcType(requirement.getProcType());
            projection.setForecastId(0);
            projection.setIntransit(0);
            projection.setInventory(0);
            projection.setPolicyId(requirement.getRequirementSnapshot().getPolicy());
            projection.setGroupId(requirement.getRequirementSnapshot().getGroup().getId());
            requirements.forEach(requirement1 -> {
                requirement1.setProjection(projection);
            });
        }

        //save
        requirementRepository.persist(allRequirements);
    }

    private void populateSupplier(List<Requirement> validRequirements) {

    }

    private Map<String, Group> getFsnToGroupMap() {
        List<GroupFsn> groupFsns = groupFsnRepository.findByFsns(forecastContext.getFsns());
        return groupFsns.stream().collect(Collectors.toMap(GroupFsn::getFsn, GroupFsn::getGroup));
    }

    private Requirement getRequirement(String fsn, String warehouse, Group group) {
        Requirement requirement = new Requirement();
        requirement.setFsn(fsn);
        requirement.setWarehouse(warehouse);
        requirement.setState(RequirementApprovalStates.PROPOSED.toString());
        requirement.setEnabled(true);
        requirement.setCurrent(true);
//        requirement.setQuantity(0);
        //TODO: do we need procType here?
        requirement.setProcType(group.getProcurementType());
        RequirementSnapshot requirementSnapshot = new RequirementSnapshot();
        requirementSnapshot.setGroup(group);
        requirementSnapshot.setPolicy(policyContext.getPolicyAsString(fsn));
        requirementSnapshot.setForecast(forecastContext.getForecastAsString(fsn, warehouse));
        requirementSnapshot.setOpenReqQty((int) onHandQuantityContext.getOpenRequirementQuantity(fsn, warehouse));
        requirementSnapshot.setPendingPoQty((int) onHandQuantityContext.getPendingPurchaseOrderQuantity(fsn, warehouse));
        requirementSnapshot.setIwitIntransitQty((int) onHandQuantityContext.getIwtQuantity(fsn, warehouse));
        requirementSnapshot.setInventoryQty((int) onHandQuantityContext.getInventoryQuantity(fsn, warehouse));
        requirement.setRequirementSnapshot(requirementSnapshot);
        return requirement;
    }

    private ForecastContext getForecastContext() {
        List<Forecast> forecasts = forecastRepository.fetchByFsns(fsns);
        ForecastContext forecastContext = new ForecastContext(objectMapper);
        forecasts.forEach(forecast -> forecastContext.addForecast(forecast.getFsn(), forecast.getWarehouse(), forecast.getForecast()));
        return forecastContext;
    }

    private PolicyContext getPolicyContext(Set<String> fsns) {
        //add group level policies to context
        Set<Long> groupIds = fsns.stream().map(fsn -> fsnToGroupMap.get(fsn).getId()).collect(Collectors.toSet());
        List<Policy> groupPolicies = policyRepository.fetchByGroup(groupIds);
        Map<Long, Policy> groupIdToPolicyMap = groupPolicies.stream().collect(Collectors.toMap(policy -> policy.getGroup().getId(), policy -> policy));
        fsns.forEach(fsn -> {
            Long groupId = fsnToGroupMap.get(fsn).getId();
            Policy policy = groupIdToPolicyMap.get(groupId);
            policyContext.addPolicy(fsn, policy.getPolicyType(), policy.getValue());
        });
        //override with fsn level policies
        List<Policy> policies = policyRepository.fetchByFsns(fsns);
        PolicyContext policyContext = new PolicyContext(objectMapper);
        policies.forEach(policy -> policyContext.addPolicy(policy.getFsn(), policy.getPolicyType(), policy.getValue()));
        return policyContext;
    }

    private OnHandQuantityContext getOnHandQuantityContext(Set<String> fsns) {
        OnHandQuantityContext onHandQuantityContext = new OnHandQuantityContext();
        //add wh inventory
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.fetchByFsns(fsns);
        warehouseInventories.forEach(warehouseInventory -> onHandQuantityContext.addInventoryQuantity(warehouseInventory.getFsn(), warehouseInventory.getWarehouse(), warehouseInventory.getQuantity()));
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
