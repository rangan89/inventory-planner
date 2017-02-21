package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.command.DownloadProposedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class ProposedRequirementState implements RequirementState {

    private final DownloadProposedCommand downloadProposedCommand;

    @Inject
    public ProposedRequirementState(DownloadProposedCommand downloadProposedCommand) {
        this.downloadProposedCommand = downloadProposedCommand;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadProposedCommand.execute(requirements, isLastAppSupplierRequired);
    }
}
