package fk.retail.ip.zulu.config;

import lombok.Data;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Data
public class ZuluConfiguration {
    String url;
    String viewPath;
    String clientId;
    int maxBatchSize;
}
