package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.DownloadBizFinReviewCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class BizFinRequirementState implements RequirementState {
    private final Provider<DownloadBizFinReviewCommand> downloadBizFinReviewCommandProvider;

    @Inject
    public BizFinRequirementState(Provider<DownloadBizFinReviewCommand> downloadBizFinReviewCommandProvider) {
        this.downloadBizFinReviewCommandProvider = downloadBizFinReviewCommandProvider;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadBizFinReviewCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
