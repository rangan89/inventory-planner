package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.command.DownloadIPCFinalisedCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class IPCFinalisedRequirementState implements RequirementState {
    private final DownloadIPCFinalisedCommand downloadIPCFinalisedCommand;

    @Inject
    public IPCFinalisedRequirementState(DownloadIPCFinalisedCommand downloadIPCFinalisedCommand) {
        this.downloadIPCFinalisedCommand = downloadIPCFinalisedCommand;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadIPCFinalisedCommand.execute(requirements, isLastAppSupplierRequired);
    }
}