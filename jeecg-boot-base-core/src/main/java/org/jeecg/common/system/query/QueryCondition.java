/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 07:10
 **/

package org.jeecg.common.system.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: QueryCondition
 * @author: Sunshine_Lin
 */

@Data
public class QueryCondition implements Serializable {

    private static final long serialVersionUID = 4740166316629191651L;

    private String field;
    /** 组件的类型（例如：input、select、radio） */
    private String type;
    /**
     * 对应的数据库字段的类型
     * 支持：int、bigDecimal、short、long、float、double、boolean
     */
    private String dbType;
    private String rule;
    private String val;

    public QueryCondition(String field, String type, String dbType, String rule, String val) {
        this.field = field;
        this.type = type;
        this.dbType = dbType;
        this.rule = rule;
        this.val = val;
    }



    @Override
    public String toString(){
        StringBuffer sb =new StringBuffer();
        if(field == null || "".equals(field)){
            return "";
        }
        sb.append(this.field).append(" ").append(this.rule).append(" ").append(this.type).append(" ").append(this.dbType).append(" ").append(this.val);
        return sb.toString();
    }
}
