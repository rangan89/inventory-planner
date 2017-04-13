package fk.retail.ip.fdp.model;

import java.util.Date;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class FdpEventPayload<T> {
    Object eventId;
    String schemaVersion;
    Date eventTime;
    T data;
}
