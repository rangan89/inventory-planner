package fk.retail.ip.requirement.internal.enums;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fk.retail.ip.requirement.config.RequirementModule;
import fk.retail.ip.requirement.internal.command.DownloadBizFinReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadCDOReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadCommand;
import fk.retail.ip.requirement.internal.command.DownloadIPCReviewCommand;
import fk.retail.ip.requirement.internal.command.DownloadProposedCommand;

/**
 * Created by nidhigupta.m on 30/01/17.
 */
public enum RequirementState {

    PROPOSED(DownloadProposedCommand.class),
    CDO_REVIEW(DownloadCDOReviewCommand.class),
    BIZFIN_REVIEW(DownloadBizFinReviewCommand.class),
    IPC_REVIEW(DownloadIPCReviewCommand.class),
    IPC_FINALIZED(DownloadIPCReviewCommand.class);

    private DownloadCommand downloadCommand;


    RequirementState(Class<? extends DownloadCommand> type) {
        Injector INJECTOR = Guice.createInjector(new RequirementModule());
        downloadCommand = INJECTOR.getInstance(type);
    }

    public DownloadCommand getDownloadCommand() {
        return this.downloadCommand;
    }

}
