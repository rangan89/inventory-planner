package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.command.DownloadIPCReviewCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class IPCReviewRequirementState implements RequirementState {
    private final DownloadIPCReviewCommand downloadIPCReviewCommand;

    @Inject
    public IPCReviewRequirementState(DownloadIPCReviewCommand downloadIPCReviewCommand) {
        this.downloadIPCReviewCommand = downloadIPCReviewCommand;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadIPCReviewCommand.execute(requirements, isLastAppSupplierRequired);
    }
}

