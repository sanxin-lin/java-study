package org.jeecg.config.firewall.SqlInjection;

public interface IDictTableWhiteListHandler {

    /**
     * 校验【表名】【字段】是否合法允许查询，允许则返回 true
     *
     * @param sql
     * @return
     */
    boolean isPassBySql(String sql);

    /**
     * 校验字典是否通过
     *
     * @param dictCodeString 字典表配置
     * @return
     */
    boolean isPassByDict(String dictCodeString);

    boolean isPassByDict(String tableName, String... fields);

    /**
     * 清空缓存，使更改生效
     *
     * @return
     */
    boolean clear();

    String getErrorMsg();
}
