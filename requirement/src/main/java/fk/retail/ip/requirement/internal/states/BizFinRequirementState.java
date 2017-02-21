package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.command.DownloadBizFinReviewCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class BizFinRequirementState implements RequirementState {
    private final DownloadBizFinReviewCommand downloadBizFinReviewCommand;

    @Inject
    public BizFinRequirementState(DownloadBizFinReviewCommand downloadBizFinReviewCommand) {
        this.downloadBizFinReviewCommand = downloadBizFinReviewCommand;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadBizFinReviewCommand.execute(requirements, isLastAppSupplierRequired);
    }
}
