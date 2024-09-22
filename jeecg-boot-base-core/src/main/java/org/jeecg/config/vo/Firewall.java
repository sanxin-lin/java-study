/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 06:52
 **/

package org.jeecg.config.vo;

import lombok.Data;

@Data
public class Firewall {
    /**
     * 数据源安全 (开启后，Online报表和图表的数据源为必填)
     */
    private Boolean dataSourceSafe = false;
    /**
     * 是否禁止使用 * 查询所有字段
     */
    private Boolean disableSelectAll = false;
    /**
     * 低代码模式（dev:开发模式，prod:发布模式——关闭所有在线开发配置能力）
     */
    private String lowCodeMode;
}
