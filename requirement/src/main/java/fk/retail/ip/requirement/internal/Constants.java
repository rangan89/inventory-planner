package fk.retail.ip.requirement.internal;

import com.google.common.collect.Sets;
import java.util.Set;

public class Constants {

    public static final double DEFAULT_FORECAST = 0.0;
    public static final int DAYS_IN_WEEK = 7;
    public static final int WEEKS_OF_FORECAST = 15;
    public static final Set<String> INTRANSIT_REQUEST_STATUSES = Sets.newHashSet("in-process", "dispatched", "requested");
    public static final String POLICY_DISPLAY_FORMAT = "{\"%s\":%.2f}";

    //Error messages
    public static final String INVALID_POLICY_TYPE = "Did not find matching policy type for: {}";
    public static final String GROUP_NOT_FOUND = "Group for this fsn is not present";
    public static final String FORECAST_NOT_FOUND = "Forecast for this fsn is not present";
    public static final String VALID_POLICY_NOT_FOUND = "Valid %s policy for this fsn is not present";
    public static final String UNABLE_TO_PARSE = "Unable to parse: {}";
    public static final String NOT_APPLICABLE = "N/A";

    public static final String ERROR_STATE = "error";
}
