package org.jeecg.config.vo;

import lombok.Data;

/**
 * @author: Sunshine_Lin
 */

@Data
public class Elasticsearch {
    private String clusterNodes;
    private boolean checkEnabled;
}
