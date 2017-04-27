package fk.retail.ip.fdp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by yogeshwari.k on 17/03/17.
 */
@Data
public class FdpEventPayload<T> {
    Object eventId;
    String schemaVersion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Date eventTime;
    T data;
}
