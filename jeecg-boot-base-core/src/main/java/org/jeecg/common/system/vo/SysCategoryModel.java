/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 12:27
 **/

package org.jeecg.common.system.vo;


import lombok.Data;

/**
 * @Author Sunshine_Lin
 * @Description:
 * @Version 1.0
 */
@Data
public class SysCategoryModel {
    /**主键*/
    private java.lang.String id;
    /**父级节点*/
    private java.lang.String pid;
    /**类型名称*/
    private java.lang.String name;
    /**类型编码*/
    private java.lang.String code;
}
