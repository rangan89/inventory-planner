package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.DownloadProposedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class ProposedRequirementState implements RequirementState {

    private final Provider<DownloadProposedCommand> downloadProposedCommandProvider;

    @Inject
    public ProposedRequirementState(Provider<DownloadProposedCommand> downloadProposedCommandProvider) {
        this.downloadProposedCommandProvider = downloadProposedCommandProvider;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadProposedCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
