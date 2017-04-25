package fk.retail.ip.requirement.config;

import lombok.Data;

@Data
public class TriggerRequirementConfiguration {

    private String url;
    private String projectionQueueName;
    private int fetchDataBatchSize = 10000;
    private int createReqBatchSize = 10;
}
