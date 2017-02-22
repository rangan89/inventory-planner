package fk.retail.ip.requirement.internal.states;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.DownloadCDOReviewCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import java.util.List;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class CDOReviewRequirementState implements RequirementState {
    private final Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider;

    @Inject
    public CDOReviewRequirementState(Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider) {
        this.downloadCDOReviewCommandProvider = downloadCDOReviewCommandProvider;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadCDOReviewCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }
}
