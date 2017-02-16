package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.repository.FsnBandRepository;
import fk.retail.ip.requirement.internal.repository.WeeklySaleRepository;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class DownloadProposedCommand extends DownloadCommand {

    @Inject
    public DownloadProposedCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand) {
        super(fsnBandRepository, weeklySaleRepository, generateExcelCommand);
    }

    @Override
    protected String getTemplateName() {
        return "/templates/proposed.xlsx";
    }

    @Override
    void fetchRequirementStateData() {

    }
}
