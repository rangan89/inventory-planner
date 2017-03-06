package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.CalculateRequirementCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.exception.NoRequirementsSelectedException;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactory;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.states.RequirementState;
import fk.retail.ip.requirement.model.CalculateRequirementRequest;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementApprovalRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

/**
 * @author nidhigupta.m
 * @author Pragalathan M <pragalathan.m@flipkart.com>
 */
@Slf4j
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementStateFactory requirementStateFactory;
    private final ApprovalService approvalService;
    private final Provider<CalculateRequirementCommand> calculateRequirementCommandProvider;

    @Inject
    public RequirementService(RequirementRepository requirementRepository, RequirementStateFactory requirementStateFactory,
                              ApprovalService approvalService, Provider<CalculateRequirementCommand> calculateRequirementCommandProvider) {
        this.requirementRepository = requirementRepository;
        this.requirementStateFactory = requirementStateFactory;
        this.approvalService = approvalService;
        this.calculateRequirementCommandProvider = calculateRequirementCommandProvider;
    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest downloadRequirementRequest) {
        List<Long> requirementIds = downloadRequirementRequest.getRequirementIds();
        String requirementState = downloadRequirementRequest.getState();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        List<Requirement> requirements;
        if (!requirementIds.isEmpty()) {
            requirements = requirementRepository.findRequirementByIds(requirementIds);
        } else {

            requirements = requirementRepository.findAllCurrentRequirements(requirementState);
        }
        //todo: cleanup remove if 'all' column value for warehouse is removed
        if (requirements.isEmpty()) {
            throw new NoRequirementsSelectedException("No requirements were selected in state " + requirementState);
        }
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        RequirementState state = requirementStateFactory.getRequirementState(requirementState);
        return state.download(requirements, isLastAppSupplierRequired);
    }

    public String changeState(RequirementApprovalRequest request) throws JSONException {
        String action = request.getFilters().get("projection_action").toString();
        Function<Requirement, String> getter = Requirement::getState;
        List<Requirement> requirements;
        List<Long> ids = (List<Long>) request.getFilters().get("id");
        String state = (String) request.getFilters().get("state");

        int count = 0;
        int pageNumber = 1;
        Set<Long> projectionIds = new HashSet<>();
        do {
            requirements = requirementRepository.findRequirements(ids, state, request.getFilters(), pageNumber++);
            count += requirements.size();
            if (requirements.isEmpty()) {
                break;
            }
            log.info("Loaded {} records from page {}", requirements.size(), pageNumber - 1);
            requirements.stream().forEach(e -> projectionIds.add(e.getProjectionId()));
            approvalService.changeState(requirements, "dummyUser", action, getter, new ApprovalService.CopyOnStateChangeAction(requirementRepository));
        } while (requirements.size() == RequirementRepository.PAGE_SIZE);

        requirementRepository.updateProjection(projectionIds, approvalService.getTargetState(action));
        return "{\"msg\":\"Moved " + projectionIds.size() + " projections to new state.\"}";
    }


    public void calculateRequirement(CalculateRequirementRequest calculateRequirementRequest) {
        calculateRequirementCommandProvider.get().withFsns(calculateRequirementRequest.getFsns()).execute();
    }
}
