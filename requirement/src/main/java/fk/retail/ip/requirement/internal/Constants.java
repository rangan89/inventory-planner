package fk.retail.ip.requirement.internal;

import com.google.common.collect.Sets;
import java.util.Set;

public interface Constants {

    double DEFAULT_FORECAST = 0.0;
    int DAYS_IN_WEEK = 7;
    int WEEKS_OF_FORECAST = 15;
    double MIN_CASE_SIZE = 1D;
    String MAX_COVERAGE_KEY = "max_coverage";
    String CASE_SIZE_KEY = "case_size";
    Set<String> INTRANSIT_REQUEST_STATUSES = Sets.newHashSet("in-process", "dispatched", "requested");
    String POLICY_DISPLAY_FORMAT = "{\"%s\":%.2f}";

    //Error messages
    String INVALID_POLICY_TYPE = "Did not find matching policy type for: {}";
    String GROUP_NOT_FOUND = "Group for this fsn is not present";
    String FORECAST_NOT_FOUND = "Forecast for this fsn is not present";
    String VALID_POLICY_NOT_FOUND = "Valid %s policy for this fsn is not present";
    String UNABLE_TO_PARSE = "Unable to parse: {}";
    String NOT_APPLICABLE = "N/A";

    String ERROR_STATE = "error";
}
