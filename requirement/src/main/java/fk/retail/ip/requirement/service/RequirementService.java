package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementManager;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementManager requirementManager;

    @Inject
    public RequirementService(RequirementRepository requirementRepository, RequirementManager requirementManager) {
        this.requirementRepository = requirementRepository;
        this.requirementManager = requirementManager;

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
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        StreamingOutput output = requirementManager.withRequirements(requirements).download(requirementState, isLastAppSupplierRequired);
        return output;

    }

}
