package fk.retail.ip.proc.config;

import lombok.Data;

/**
 * Created by yogeshwari.k on 07/04/17.
 */
@Data
public class ProcClientConfiguration {
    String viewPath;
    String url;
    String callbackUrl;
    String requirementQueueName;
}
